/**
 *  StaticQuery
 *
 *  Copyright 2017 Tao Long
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
import groovy.json.JsonSlurper
 
definition(
    name: "StaticQuery",
    namespace: "antloog",
    author: "Tao Long",
    description: "Static query of the state of the devices",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Battery Group") {
       input "group_batteries", "capability.battery", title: "Select battery powered devices", multiple: true, required: false
    }
    
    section("Lock Group") {
       input "group_locks", "capability.lock", title: "Select lock devices", multiple: true, required: false
    }
    
    section("Temperature Group") {
       input "group_temperature", "capability.temperatureMeasurement", title: "Select temperature devices", multiple: true, required: false
    }

    section("Switch Group") {
       input "group_switches", "capability.light", title: "Select switch devices", multiple: true, required: false
    }
    
    section("Motion Group") {
       input "group_motion", "capability.motionSensor", title: "Select switch devices", multiple: true, required: false
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	state.lock_codes = [:]
    for (lock in settings["group_locks"]) {
        subscribe(lock, "codeReport", lockEventHandler, [filterEvents:false])
        log.debug "Subscribing to ${lock}: [${lock.getId()}]"
        state.lock_codes[lock.getId()] = [:]
    }
}

def lockEventHandler(evt) {
    log.debug "Lock event!"
    def data = new JsonSlurper().parseText(evt.data)
	log.debug evt.data
    def code = data.code
    def device = evt.getDevice()
    def device_id = device.getId()
    def slot = evt.value
    def entry = [
    	name: evt.name, 
        device_id: device_id, 
        slot: slot, 
        code: code
    ]
    log.debug entry
    state.lock_codes[device_id][slot] = code
    log.debug state
}

def lockHandler(event){
    log.debug "Lock $event"
}

mappings {
	path("/list"){
    	action: [GET: "list"]
    }
    
    path("/battery") {
        action: [GET: "getBattery"]
    }
    
    path("/lock") {
        action: [GET: "getLock"]
    }
    
    path("/temperature") {
        action: [GET: "getTemperature"]
    }

    path("/switch") {
        action: [GET: "getSwitch"]
    }
    
    path("/motion") {
        action: [GET: "getMotion"]
    }
    
    path("/lock_codes"){
    	action: [GET: "lock_codes"]
    }
}

def list(){
	def response = []
    response << [app: "StaticQuery", version: "1.0"]
    response << [endpoint: "/battary", description: "battery level (0-100), interger"]
    response << [endpoint: "/lock", description: "lock status, 'unlocked' or 'locked'" ]
    response << [endpoint: "/temperature", description: "termperature value in F"]
    response << [endpoint: "/switch", description: "switch status, either 'on' or 'off'"]
    response << [endpoint: "/motion", description: "motion"]
    response << [endpoint: "/lock_codes", description: "retrieve known lock codes"]
    return response
}

def getBattery() {
	def response = []
    settings["group_batteries"].each {
        response << [id: it.id, name: it.displayName, value: it.currentValue("battery")]
    }
    return response
}

def getLock() {
	def response = []
    settings["group_locks"].each {
        response << [id: it.id, name: it.displayName, value: it.currentValue("lock")]
    }
    return response
}

def getTemperature() {
	def response = []
    settings["group_temperature"].each {
        response << [id: it.id, name: it.displayName, value: it.currentValue("temperature")]
    }
    return response
}

def getSwitch() {
	def response = []
    settings["group_switches"].each {
        response << [id: it.id, name: it.displayName, value: it.currentValue("switch")]
    }
    return response
}

def getMotion() {
	def response = []
    settings["group_motion"].each {
        response << [id: it.id, name: it.displayName, value: it.currentValue("motion")]
    }
    return response
}

def lock_codes(){
	def response = []
    response = state.lock_codes
    return state.lock_codes
}
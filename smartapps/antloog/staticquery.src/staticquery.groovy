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
    settings["group_locks"].each{
        log.debug "subscribing to $it"
        subscribe(it, "lock", lockHandler, [isStateChange: true])
    }
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
}

def list(){
	def response = []
    response << [app: "StaticQuery", version: "1.0"]
    response << [endpoint: "/battary", description: "battery level (0-100), interger"]
    response << [endpoint: "/lock", description: "lock status, 'unlocked' or 'locked'" ]
    response << [endpoint: "/temperature", description: "termperature value in F"]
    response << [endpoint: "/switch", description: "switch status, either 'on' or 'off'"]
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
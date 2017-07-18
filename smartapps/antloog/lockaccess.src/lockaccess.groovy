/**
 *  LockAccess
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
    name: "LockAccess",
    namespace: "antloog",
    author: "Tao Long",
    description: "Manage lock access codes",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

	
preferences {
    section("Lock Group") {
       input "group_locks", "capability.lock", title: "Select lock devices", multiple: true, required: false
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
	// TODO: subscribe to attributes, devices, locations, etc.
    settings["group_locks"].each{
        log.debug "subscribing to $it"
        subscribe(it, "lock", lockHandler, [isStateChange: true])
    }
}

mappings {
	path("/list"){
    	action: [GET: "list"]
    }
    
    path("/set_code") {
        action: [POST: "setCode"]
    }

    path("/delete_code") {
        action: [POST: "deleteCode"]
    }
}

def lockHandler(event){
    log.debug "Lock $event"
}

def setCode(){
    def response = []
    def json = request.JSON
    response << [device: json["device"], code: json["code"]]
    return response
}

def deleteCode(){
    def response = []

    return response
}
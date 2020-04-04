/**
 *  Ring Alarm - SmartThings integration
 *
 *  Licence Details.
 *	https://opensource.org/licenses/MIT
 *
 *  Copyright 2019 Asish Soudhamma
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 *  and associated documentation files (the "Software"), to deal in the Software without restriction, 
 *  including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 *  sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial 
 *  portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
metadata {
	definition (name: "Ring Alarm", namespace: "asishrs", author: "Asish Soudhamma", cstHandler: true) {
		capability "Alarm"
		capability "Polling"
        capability "Presence Sensor"
		command "off"
		command "home"
		command "away"
        command "triggeroff"
		command "triggerhome"
		command "triggeraway"
		command "update_state"
        command "createChildDevices"
        command "refreshDeviceStatus"
        command "refreshDevices"
        command "updateEventData"
        command "removeChildDevices"
		attribute "events", "string"
		attribute "messages", "string"
		attribute "status", "string"
        attribute "mode", "string"
        attribute "alarmStatus", "string"
        singleInstance: true
	}

	tiles(scale: 2) {
        multiAttributeTile(name:"status", type: "generic", width: 6, height: 4) {
            tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
                attributeState "off", label:'disarmed', icon: "st.security.alarm.off", backgroundColor: "#1998d5"
                attributeState "home", label:'${name}', icon: "st.Home.home4", backgroundColor: "#e58435"
                attributeState "away", label:'${name}', icon: "st.security.alarm.on", backgroundColor: "#e53935"
                attributeState "pending off", label:'pending disarmed', icon: "st.security.alarm.off", backgroundColor: "#ffffff"
                attributeState "pending away", label:'${name}', icon: "st.Home.home4", backgroundColor: "#ffffff"
                attributeState "pending home", label:'${name}', icon: "st.security.alarm.on", backgroundColor: "#ffffff"
                attributeState "away_count", label:'countdown', icon: "st.security.alarm.on", backgroundColor: "#ffffff"
                attributeState "failed set", label:'error', icon: "st.secondary.refresh", backgroundColor: "#d44556"
                attributeState "alert", label:'${name}', icon: "st.alarm.beep.beep", backgroundColor: "#ffa81e"
                attributeState "alarm", label:'${name}', icon: "st.security.alarm.alarm", backgroundColor: "#d44556"
            }
        }	

        standardTile("off", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state ("off", label:"disarmed", action:"triggeroff", icon: "st.security.alarm.off", backgrosundColor: "#008CC1", nextState: "pending")
            state ("away", label:"disarmed", action:"triggeroff", icon: "st.security.alarm.off", backgroundColor: "#505050", nextState: "pending")
            state ("home", label:"disarmed", action:"triggeroff", icon: "st.security.alarm.off", backgroundColor: "#505050", nextState: "pending")
            state ("pending", label:"pending", icon: "st.security.alarm.off", backgroundColor: "#ffffff")
        }
        
        standardTile("home", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state ("off", label:"home", action:"triggerhome", icon: "st.Home.home4", backgroundColor: "#505050", nextState: "pending")
            state ("away", label:"home", action:"triggerhome", icon: "st.Home.home4", backgroundColor: "#505050", nextState: "pending")
            state ("home", label:"home", action:"triggerhome", icon: "st.Home.home4", backgroundColor: "#008CC1", nextState: "pending")
            state ("pending", label:"pending", icon: "st.Home.home4", backgroundColor: "#ffffff")
        }

        standardTile("away", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
            state ("off", label:"away", action:"triggeraway", icon: "st.security.alarm.on", backgroundColor: "#505050", nextState: "pending") 
            state ("away", label:"away", action:"triggeraway", icon: "st.security.alarm.on", backgroundColor: "#008CC1", nextState: "pending")
            state ("home", label:"away", action:"triggeraway", icon: "st.security.alarm.on", backgroundColor: "#505050", nextState: "pending")
            state ("pending", label:"pending", icon: "st.security.alarm.on", backgroundColor: "#ffffff")
            state ("away_count", label:"pending", icon: "st.security.alarm.on", backgroundColor: "#ffffff")
        }

        // Base Station
        standardTile("ringbase", "device.ringbase", label: "Base Station", decoration: "flat", width: 6, height: 1) {
                state("unknown", label: '${name}', icon: "st.unknown.unknown.unknown", backgroundColor: "#505050")
                state("online", label: '${name}',icon: "st.security.alarm.clear", backgroundColor: "#ffffff")
                state("offline", label: '${name}', icon: "st.alarm.alarm.alarm", backgroundColor: "#00a0dc")   
        }
        
        def rangeExtenderCount = 1
        def keypadCount = 1

        // Range Extender
        (1..rangeExtenderCount).each { n ->	
            standardTile("ringrange$n", "device.ringrange$n", decoration: "flat", width: 2, height: 2) {
                state("unknown", label: '${name}', icon: "st.unknown.unknown.unknown", backgroundColor: "#505050")
                state("online", label: '${name}',icon: "st.samsung.da.RC_ic_charge", backgroundColor: "#ffffff")
                state("offline", label: '${name}', icon: "st.samsung.da.RC_ic_charge", backgroundColor: "#e86d13")
            }

        }

        // Keypad
        (1..keypadCount).each { n ->	
            standardTile("ringkeypad$n", "device.ringkeypad$n", decoration: "flat", width: 2, height: 2) {
                state("unknown", label: '${name}', icon: "st.unknown.unknown.unknown", backgroundColor: "#505050")
                state("online", label: '${name}',icon: "st.Home.home3", backgroundColor: "#ffffff")
                state("offline", label: '${name}', icon: "st.Home.home3", backgroundColor: "#e86d13")
            }

        }
        
        // Tile for showing Ring Events as Texts
        valueTile("log", "device.log", decoration: "flat", width: 6, height: 3) {
            state "poweron", label: '${currentValue}'
        }

        main(["status"])
    }
}

def installed() {
 	init()
}

def updated() {
    unschedule()
    //def originalChildDevices = getChildDevices()
    //def addedChildren = createChildDevices()
    // Remove devices that are no longer needed (assumes device order hasn't changed)
    //def noLongerNeededDevices = deduct(originalChildDevices, addedChildren)
    //removeChildDevices(noLongerNeededDevices)
    // Now start polling
    init()
}

def deduct(originalChildDevices, addedChildren) {
	// TODO: Maybe a groovier way to do this?
	def result = originalChildDevices.clone()
	addedChildren.each { toRemove -> 
    	result.removeAll { it.deviceNetworkId == toRemove.deviceNetworkId }
    }

    return result
}

def uninstalled() {
	removeChildDevices()
}

def removeChildDevices() {
    def children = getChildDevices()
	log.info "removeChildDevices() -> Deleting devices: ${children}"
    children.each {
    	log.info "removeChildDevices() -> Trying to delete device ${it.deviceNetworkId}"
        try {
        	deleteChildDevice(it.deviceNetworkId)
        } catch (Exception failedDeletingException) {
        	log.error "removeChildDevices() -> Error deleting device {$it.deviceNetworkId}, remove usages and try updating preferences later. - ${failedDeletingException}"
        }
    }
}

def createChildDevices(ringAPIData) {
	log.debug "createChildDevices() -> Searching for contact and motion sensors on Ring Alarm ${device.deviceNetworkId} with device ${ringAPIData}"
    for (ringDevice in ringAPIData.deviceStatus) {
    	//log.trace "Ring Device to add/update - ${ringDevice}"
        switch (ringDevice.type) {
            case 'sensor.contact' :
                addSensor("Contact", ringDevice.id, ringDevice.name)
            	break
            case 'sensor.motion' :
                addSensor("Motion", ringDevice.id, ringDevice.name)
                break   
            case 'sensor.flood-freeze' :
                addSensor("Floodfreeze", ringDevice.id, ringDevice.name)
                break 
            case 'listener.smoke-co' :
                addSensor("Smoke CO", ringDevice.id, ringDevice.name)
                break 
        }
    }
}

def addSensor(type, id, name) {
	def deviceId = "${device.deviceNetworkId}-${id}";
	def currentChildren = getChildDevices()
    def alreadyExistingChild = currentChildren?.find { it.deviceNetworkId == deviceId}

    if (alreadyExistingChild) {
    	//log.trace "addSensor() -> Sensor with type ${type} and ${deviceId} already exists, not adding it again."
        return alreadyExistingChild
    } else {
        log.debug "addSensor() -> Adding sensor with type ${type} and ${deviceId}"
        //DeviceWrapper addChildDevice(String typeName, String deviceNetworkId, hubId, Map properties)
        return addChildDevice("Ring Alarm ${type} Sensor", deviceId, device.hubId,
                              [isComponent: false, completedSetup: true, componentName: "${type}-${deviceId}", 
                               componentLabel: "Ring ${name}", label: "Ring ${name}"])
	}
}

def init() {
	//
}

def triggeroff() {
	log.info "triggeroff() -> Setting Ring Alarm mode to 'Off'"
	callApiAndUpdateEvents('off')
}

def triggerhome() { 
	log.info "triggerhome() -> Setting Ring Alarm mode to 'Home'"
	callApiAndUpdateEvents('home')
}

def triggeraway() {
	log.info "triggeraway() -> Setting Ring Alarm mode to 'Away'"
	callApiAndUpdateEvents('away')
}

def off() {
	log.info "off() -> Setting Ring Alarm mode to 'Off'"
	sendEvent(name: "status", value: "off")
}

def home() { 
	log.info "home() -> Setting Ring Alarm mode to 'Home'"
	sendEvent(name: "status", value: "home")
}

def away() {
	log.info "away() -> Setting Ring Alarm mode to 'Away'"
	sendEvent(name: "status", value: "away")
}

def update_state() {
	log.info "update_state() -> Refreshing Ring AlarmV2 state..."
	refreshDevices()
}

def callApiAndUpdateEvents(ringMode) {
    sendEvent(name: 'mode', value: ringMode)
    sendEvent(name: 'alarm', value: ringMode)
    sendEvent(name: "status", value: "pending ${ringMode}")
}

def humanReadableAlarmStatus(val) {
	//log.info "Ring Status (API) ${val}"
    def result
    switch (val) {
        case 'none':
            result = 'off'
            break
        case 'some':
            result = 'home'
            break
        case 'all':
            result = 'away'
            break
        case 'alarming':
            result = 'alarm'
            break
        case 'exit-delay':
            result = 'away_count'
            break
        case 'alarm-cleared':
            result = 'alert'
            break
        default:
            result = 'off'
            break
    }   
    //log.info "humanReadableAlarmStatus() -> Ring Status (Display) ${result}"
    result
}

def humanReadableEventStatus(val) {
	//log.info "Ring Event (API) ${val}"
    def result
    switch (val) {
        case 'security-panel.alarming':
            result = 'alarm'
            break
        case 'security-panel.exit-delay':
            result = 'away_count'
            break
        case 'security-panel.alarm-cleared':
            result = 'alert'
            break
        default:
            result = ''
            break
    }   
    //log.info "humanReadableEventStatus() -> Ring Event (Display) ${result}"
    result
}

public updatePref(val) {
	log.info "Ring Event Shared value ${val}"
}

def refreshDeviceStatus(ringDeviceStatus) {
    def keypadCount = 0
    def rangeExtenderCount = 0
    def alarmNetworkId = device.deviceNetworkId
    def childSensors = getChildDevices();

    for (device in ringDeviceStatus) {
        //log.debug "refreshDeviceStatus() -> Ring Device: Id - ${device.id}, Name - ${device.name}, Type - ${device.type}, Faulted - ${device.faulted}, Mode - ${device.mode}"
     
        if (device.type == 'security-panel') {
            def alarmStatus = humanReadableAlarmStatus(device.mode)
            log.debug "refreshDeviceStatus() -> Ring Alarm Status: ${device.mode}, ${alarmStatus}"
            sendEvent(name: "alarm", value: alarmStatus)
            sendEvent(name: "status", value: alarmStatus)
            sendEvent(name: 'presence', value: alarmStatus)
        } 

        switch (device.type) {
            case 'sensor.contact' :
            	def contactSensor = childSensors?.find { it.deviceNetworkId == "${alarmNetworkId}-${device.id}" }
				//log.trace "refreshDeviceStatus() -> Contact Sensor ${contactSensor} found with Id ${alarmNetworkId}-${device.id}, updating status."
                if(contactSensor) {
                    if (device.faulted) 
                        contactSensor.sendEvent(name: "contact", value: "open", isStateChange: true, descriptionText: "${device.name} is Open")
                    else
                        contactSensor.sendEvent(name: "contact", value: "closed", isStateChange: true, descriptionText: "${device.name} is Closed")
                }
                break
            case 'sensor.flood-freeze' :
                def floodFreezeSensor = childSensors?.find { it.deviceNetworkId == "${alarmNetworkId}-${device.id}"}
				//log.trace "refreshDeviceStatus() -> Flood Freeze Sensor ${floodFreezeSensor} found with Id ${alarmNetworkId}-${device.id}, updating status."
                if(floodFreezeSensor){
                	if (device.faulted) 
                        floodFreezeSensor.sendEvent(name: "water", value: "wet", isStateChange: true, descriptionText: "${device.name} is Wet")
                    else
                        floodFreezeSensor.sendEvent(name: "water", value: "dry", isStateChange: true, descriptionText: "${device.name} is Dry")
                }
                break
            case 'range-extender.zwave' :
            	//No Chile devices Yet for Range Extendar
                if (device.faulted) 
                	sendEvent(name: "ringrange${++rangeExtenderCount}", value: "offline", isStateChange: true, descriptionText: "${device.name} is Offline")
                else
                    sendEvent(name: "ringrange${++rangeExtenderCount}", value: "online", isStateChange: true, descriptionText: "${device.name} is Online")
                break
            case 'security-keypad' :
            	//No Chile devices Yet for Range Extendar
                if (device.faulted) 
                	sendEvent(name: "ringkeypad${++keypadCount}", value: "offline", isStateChange: true, descriptionText: "${device.name} is Offline")
                else
                    sendEvent(name: "ringkeypad${++keypadCount}", value: "online", isStateChange: true, descriptionText: "${device.name} is Online")
                break
            case 'sensor.motion' :
            	def motionSensor = childSensors?.find { it.deviceNetworkId == "${alarmNetworkId}-${device.id}"}
				//log.trace "refreshDeviceStatus() -> Motion Sensor ${floodFreezeSensor} found with Id ${alarmNetworkId}-${device.id}, updating status."
                if(motionSensor) {
                    if (device.faulted) 
                        motionSensor.sendEvent(name: "motion", value: "active", isStateChange: true, descriptionText: "${device.name} - Motion Detected")
                    else
                        motionSensor.sendEvent(name: "motion", value: "inactive", isStateChange: true, , descriptionText: "${device.name} - Stopped Detecting Motion")
                }
                break  
            case 'listener.smoke-co' :
            	def smokeCOSensor = childSensors?.find { it.deviceNetworkId == "${alarmNetworkId}-${device.id}"}
				//log.trace "refreshDeviceStatus() -> Motion Sensor ${floodFreezeSensor} found with Id ${alarmNetworkId}-${device.id}, updating status."
                if(smokeCOSensor) {
                    if (device.faulted) 
                        smokeCOSensor.sendEvent(name: "smokeco", value: "alarm", isStateChange: true, descriptionText: "${device.name} - Detected Smoke/CO")
                    else
                        smokeCOSensor.sendEvent(name: "smokeco", value: "clear", isStateChange: true, , descriptionText: "${device.name} - clear")
                }
                break    
            case 'hub.redsky' :
            	//No Chile devices Yet for Range Extendar
                if (device.faulted) 
                	sendEvent(name: "ringbase", value: "offline", isStateChange: true, descriptionText: "${device.name} is Offline")
                else
                    sendEvent(name: "ringbase", value: "online", isStateChange: true, descriptionText: "${device.name} is Online")
                break
        }
    }
}

def updateEventData(ringEvents, pollingInterval) {
    log.trace "updateLogs() -> Processing Logs: ${ringEvents}"
    
    def logs = new StringBuilder()
    def notifyUser = true
    
    for (event in ringEvents) {
        //log.debug "updateLogs -> Ring Event: ${event.name}, ${event.type}, ${event.time}" 
        
        if(notifyUser) {
            def nowTime = new Date().time
            //log.debug "updateLogs() -> Dates Now ${nowTime}, Event ${event.time}, Polling Interval ${pollingInterval}"
            // (current time in milli - event time in milli) > interval * 60000
            //log.debug "updateLogs() -> Diff ${(nowTime - event.time)}, Time ${(pollingInterval * 60000)}"
            if ((nowTime - event.time) < (pollingInterval * 60000)) {
                //log.debug "updateLogs() -> Checking event to Alarm ${event.name}, ${event.type}, ${event.time}"
                def eventStatus = humanReadableEventStatus(event.type)
                if (eventStatus.length() > 0) {
                    sendEvent(name: "alarm", value: eventStatus)
                    sendEvent(name: "status", value: eventStatus)
                    sendEvent(name: 'presence', value: eventStatus)
                    notifyUser = false
                }

            }
        }
        
        def eventTime = Calendar.getInstance(location.getTimeZone())
        eventTime.setTimeInMillis(event.time)
        logs.append(eventTime.format("MM/dd HH:mm:ss zzz")).append("-").append(event.name).append(" - ").append(event.type).append("\r\n")
    }
    
    //log.trace "updateLogs() -> Log to Display: ${logs.toString()}"
    sendEvent(name: "log", value: logs.toString(), displayed: false)
}
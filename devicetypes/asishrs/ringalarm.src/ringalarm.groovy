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

preferences {
	input(name: "username", type: "text", title: "Username", required: "true", description: "Ring Alarm Username")
	input(name: "password", type: "password", title: "Password", required: "true", description: "Ring Alarm Password")
	input(name: "apiurl", type: "text", title: "API Url", required: "true", description: "Ring Alarm AWS API URL")
	input(name: "apikey", type: "text", title: "API Key", required: "true", description: "Ring Alarm API Api Key")
	input(name: "locationId", type: "text", title: "Location Id", required: "false", description: "Ring Alarm Location Id")
	input(name: "zid", type: "text", title: "ZID", required: "false", description: "Ring Alarm ZID")
    input(name: "pollInterval", type: "enum", title: "Polling Interval", required: "true", options: ["1 minute", "5 minutes", "10 minutes", "15 minutes"], defaultValue: "5")
}

metadata {	
	definition (name: "RingAlarmV2", namespace: "asishrs", author: "Asish Soudhamma") {
		capability "Alarm"
		capability "Polling"
        capability "Contact Sensor"
		command "off"
		command "home"
		command "away"
		command "update_state"
		attribute "events", "string"
		attribute "messages", "string"
		attribute "status", "string"
	}

	tiles(scale: 2) {
    multiAttributeTile(name:"status", type: "generic", width: 6, height: 4){
        tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
            attributeState "off", label:'${name}', icon: "st.security.alarm.off", backgroundColor: "#1998d5"
            attributeState "home", label:'${name}', icon: "st.Home.home4", backgroundColor: "#e58435"
            attributeState "away", label:'${name}', icon: "st.security.alarm.on", backgroundColor: "#e53935"
			attributeState "pending off", label:'${name}', icon: "st.security.alarm.off", backgroundColor: "#ffffff"
			attributeState "pending away", label:'${name}', icon: "st.Home.home4", backgroundColor: "#ffffff"
			attributeState "pending home", label:'${name}', icon: "st.security.alarm.on", backgroundColor: "#ffffff"
			attributeState "away_count", label:'countdown', icon: "st.security.alarm.on", backgroundColor: "#ffffff"
			attributeState "failed set", label:'error', icon: "st.secondary.refresh", backgroundColor: "#d44556"
			attributeState "alert", label:'${name}', icon: "st.alarm.beep.beep", backgroundColor: "#ffa81e"
			attributeState "alarm", label:'${name}', icon: "st.security.alarm.alarm", backgroundColor: "#d44556"
        }
    }	
	
    standardTile("off", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
        state ("off", label:"off", action:"off", icon: "st.security.alarm.off", backgrosundColor: "#008CC1", nextState: "pending")
        state ("away", label:"off", action:"off", icon: "st.security.alarm.off", backgroundColor: "#505050", nextState: "pending")
        state ("home", label:"off", action:"off", icon: "st.security.alarm.off", backgroundColor: "#505050", nextState: "pending")
        state ("pending", label:"pending", icon: "st.security.alarm.off", backgroundColor: "#ffffff")
	}
	
    standardTile("away", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
        state ("off", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#505050", nextState: "pending") 
		state ("away", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#008CC1", nextState: "pending")
        state ("home", label:"away", action:"away", icon: "st.security.alarm.on", backgroundColor: "#505050", nextState: "pending")
		state ("pending", label:"pending", icon: "st.security.alarm.on", backgroundColor: "#ffffff")
		state ("away_count", label:"pending", icon: "st.security.alarm.on", backgroundColor: "#ffffff")
	}
	
    standardTile("home", "device.alarm", width: 2, height: 2, canChangeIcon: false, inactiveLabel: true, canChangeBackground: false) {
        state ("off", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#505050", nextState: "pending")
        state ("away", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#505050", nextState: "pending")
		state ("home", label:"home", action:"home", icon: "st.Home.home4", backgroundColor: "#008CC1", nextState: "pending")
		state ("pending", label:"pending", icon: "st.Home.home4", backgroundColor: "#ffffff")
	}
    
    // Base Station
    standardTile("ringbase", "device.ringbase", label: "Base Station", decoration: "flat", width: 6, height: 1) {
            state("unknown", label: '${name}', icon: "st.unknown.unknown.unknown", backgroundColor: "#505050")
            state("online", label: '${name}',icon: "st.security.alarm.clear", backgroundColor: "#ffffff")
   		 	state("offline", label: '${name}', icon: "st.alarm.alarm.alarm", backgroundColor: "#00a0dc")   
	}
    
    //Define number of devices here.
    def motionSensorCount = 5
    def contactSensorCount = 6
    def rangeExtenderCount = 1
    def keypadCount = 1
    
    //Motion Sensor
    (1..motionSensorCount).each { n ->	
    	//log.info("Adding Motion Sensor - ${n}")
        standardTile("ringmotion$n", "device.ringmotion$n", decoration: "flat", width: 2, height: 2) {
            state("unknown", label: '${name}', icon: "st.unknown.unknown.unknown", backgroundColor: "#505050")
            state("clear", label: '${name}', icon: "st.motion.motion.inactive", backgroundColor: "#00A0DC")
       		state("motion", label: '${name}',icon: "st.motion.motion.active", backgroundColor: "#e86d13")
        }
        
	}
    
    // Contact Sensor
    (1..contactSensorCount).each { n ->	
    	//log.info("Adding Contact Sensor - ${n}")
       	standardTile("ringcontact$n", "device.ringcontact$n", decoration: "flat", width: 2, height: 2) {
        	state("unknown", label: '${name}', icon: "st.unknown.unknown.unknown", backgroundColor: "#505050")
        	state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13")     
        }
        
	}
    
    // Range Extender
    (1..rangeExtenderCount).each { n ->	
    	//log.info("Adding Range Extender - ${n}")
       	standardTile("ringrange$n", "device.ringrange$n", decoration: "flat", width: 2, height: 2) {
        	state("unknown", label: '${name}', icon: "st.unknown.unknown.unknown", backgroundColor: "#505050")
            state("online", label: '${name}',icon: "st.samsung.da.RC_ic_charge", backgroundColor: "#ffffff")
   		 	state("offline", label: '${name}', icon: "st.samsung.da.RC_ic_charge", backgroundColor: "#e86d13")
		}
        
	}
          
    // Keypad
    (1..keypadCount).each { n ->	
    	//log.info("Adding Keypad - ${n}")
       	standardTile("ringkeypad$n", "device.ringkeypad$n", decoration: "flat", width: 2, height: 2) {
            state("unknown", label: '${name}', icon: "st.unknown.unknown.unknown", backgroundColor: "#505050")
            state("online", label: '${name}',icon: "st.Home.home3", backgroundColor: "#ffffff")
   		 	state("offline", label: '${name}', icon: "st.Home.home3", backgroundColor: "#e86d13")
		}
        
	}
    
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
  init()
}
  
def init() {
	log.info "Setting up Schedule (every ${settings.pollInterval})..."
    switch(settings.pollInterval) {
    	case "1 minute" : 
        	runEvery1Minute(poll)
            break
        case "5 minutes" :
        	runEvery5Minutes(poll)
            break
        case "10 minutes" :
        	runEvery10Minutes(poll)
            break
        case "15 minutes" :
        	runEvery15Minutes(poll)
            break
     	default:
            runEvery5Minutes(poll)
            break
    }
}

def pollingInterval() {
	def value = 5
    switch(settings.pollInterval) {
    	case "1 minute" : 
        	value = 1
            break
        case "5 minutes" :
        	value = 5
            break
        case "10 minutes" :
        	value = 10
            break
        case "15 minutes" :
        	value = 15
            break
     	default:
            value = 5
            break
    }
    
    value
}

def off() {
	log.info "Setting Ring Alarm mode to 'Off'"
	ringApiCall ('off')
}

def home() { 
	log.info "Setting Ring Alarm mode to 'Home'"
	ringApiCall ('home')
}

def away() {
	log.info "Setting Ring Alarm mode to 'Away'"
	ringApiCall ('away')
}

def update_state() {
	log.info "Refreshing Ring AlarmV2 state..."
	poll()
}

def ringApiCall(state){
	def timeout = false;
	def params = [
		uri: "${settings.apiurl}/${state}",
		headers: [
			'x-api-key':settings.apikey
		],
		body: [
			user: settings.username,
			password: settings.password,
			locationId: settings.locationId,
			zid: settings.zid,
            historyLimit: 10
		]
	]

	try {
		httpPostJson(params) { resp ->
			log.debug "Ring Alarm ${state.toUpperCase()} response data: ${resp.data}"
		}
		sendEvent(name: 'alarm', value: state)
		sendEvent(name: "status", value: state)
		sendEvent(name: 'presence', value: state)
	} catch (e) {
		timeout = true
		log.debug "Ring Alarm SET to ${state.toUpperCase()} Error: $e"
	}

	if (!timeout) {
    	runIn(2, poll)
    } else {
    	runIn(10, poll)
    }
}

def humanReadableAlarmStatus(val) {
	log.info "Ring Status (API) ${val}"
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
    log.info "Ring Status (Display) ${result}"
    result
}

def humanReadableEventStatus(val) {
	log.info "Ring Event (API) ${val}"
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
    log.info "Ring Event (Display) ${result}"
    result
}

def poll() {
    log.info "Checking Ring Alarm Status V2."
	def params = [
		uri: "${settings.apiurl}/status",
		headers: [
        	'x-api-key':settings.apikey
		],
		body: [
			user: settings.username,
			password: settings.password,
			locationId: settings.locationId,
			zid: settings.zid,
            historyLimit: 10
		]
	]

	try {
		httpPostJson(params) { resp ->
        	log.debug "Ring Alarm Status Response data: ${resp.data}"
            def contactCount = 0
            def motionCount = 0
            def keypadCount = 0
            def rangeExtenderCount = 0
            for (device in resp.data.deviceStatus) {
            	log.debug "Ring Device: ${device.name}, ${device.type}, ${device.faulted}, ${device.mode}"
                if (device.type == 'security-panel') {
                	def alarmStatus = humanReadableAlarmStatus(device.mode)
                	log.debug "Ring Alarm Status: ${device.mode}, ${alarmStatus}"
                    sendEvent(name: "alarm", value: alarmStatus)
                    sendEvent(name: "status", value: alarmStatus)
                    sendEvent(name: 'presence', value: alarmStatus)
                } 
                
                log.debug "Current Count [contactCount - ${contactCount}], [motionCount - ${motionCount}], [keypadCount - ${keypadCount}], [rangeExtenderCount - ${rangeExtenderCount}]"
                switch (device.type) {
                    case 'sensor.contact' :
                        if (device.faulted) 
                        	sendEvent(name: "ringcontact${++contactCount}", value: "open", isStateChange: true, descriptionText: "${device.name} is Open")
                        else
                            sendEvent(name: "ringcontact${++contactCount}", value: "closed", isStateChange: true, descriptionText: "${device.name} is Closed")
                        break
                    case 'range-extender.zwave' :
                        if (device.faulted) 
                            sendEvent(name: "ringrange${++rangeExtenderCount}", value: "offline", isStateChange: true, descriptionText: "${device.name} is Offline")
                        else
                             sendEvent(name: "ringrange${++rangeExtenderCount}", value: "online", isStateChange: true, descriptionText: "${device.name} is Online")
                        break
                    case 'security-keypad' :
                        if (device.faulted) 
                            sendEvent(name: "ringkeypad${++keypadCount}", value: "offline", isStateChange: true, descriptionText: "${device.name} is Offline")
                        else
                            sendEvent(name: "ringkeypad${++keypadCount}", value: "online", isStateChange: true, descriptionText: "${device.name} is Online")
                        break
                    case 'sensor.motion' :
                        if (device.faulted) 
                            sendEvent(name: "ringmotion${++motionCount}", value: "motion", isStateChange: true, descriptionText: "${device.name} - Motion Detected")
                        else
                            sendEvent(name: "ringmotion${++motionCount}", value: "clear", isStateChange: true, , descriptionText: "${device.name} - Stopped Detecting Motion")
                        break   
                    case 'hub.redsky' :
                        if (device.faulted) 
                            sendEvent(name: "ringbase", value: "offline", isStateChange: true, descriptionText: "${device.name} is Online")
                        else
                            sendEvent(name: "ringbase", value: "online", isStateChange: true, descriptionText: "${device.name} is Offline")
                        break
                }
                
            }
            log.debug "Processing Logs: ${resp.data.events}"
            def logs = new StringBuilder()
            def notifyUser = true
            for (event in resp.data.events) {
            	log.debug "Ring Event: ${event.name}, ${event.type}, ${event.time}" 
                if(notifyUser) {
                    def nowTime = new Date().time
                    log.debug "Dates Now ${nowTime}, Event ${event.time}"
                    // (current time in milli - event time in milli) > interval * 60000
                    log.debug "Diff ${(nowTime - event.time)}, Time ${(pollingInterval() * 60000)}"
                    if ((nowTime - event.time) < (pollingInterval() * 60000)) {
                        log.debug "Checking event to Alarm ${event.name}, ${event.type}, ${event.time}"
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
            log.debug "Log to Display: ${logs.toString()}"
            sendEvent(name: "log", value: logs.toString(), displayed: false)
		}
	} catch (e) {
		log.debug "Ring Alarm Status check Error: $e"
	}
}
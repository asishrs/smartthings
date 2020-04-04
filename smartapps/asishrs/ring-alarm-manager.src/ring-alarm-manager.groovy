/**
 *  Ring Alarm

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
 */
definition(
	name: "Ring Alarm Manager",
    namespace: "asishrs", author: "Asish Soudhamma",
	description: "Manage the SmartThings Ring Alarm integration",
	category: "My Apps",
	iconUrl: "https://cdn.shopify.com/s/files/1/2922/1686/t/2/assets/ring_logo.png?8137716793231487980",
	iconX2Url: "https://cdn.shopify.com/s/files/1/2922/1686/t/2/assets/ring_logo.png?8137716793231487980"
)

preferences {
	page(name: "pageStart")
    page(name: "ringApiSettings") 
    page(name: "finishRingAccount2FA")
    page(name: "ringAccountStatus")
    page(name: "awsAPISettings") 
    page(name: "alarmMonitoring")            
    page(name: "notifications")
}

def pageStart(){
	dynamicPage(name: "pageStart", title: "Ring Alarm SmartThings", install: true, uninstall: true) {
    	section() {
        	href "awsAPISettings", title:"AWS API for SmartThings Integration", description: awsAPISettingsDescription()
        }
        
        section(){
        	href "ringApiSettings", title:"Ring Account", description: ringAPISettingsDescription()
        }
        
        section() {
        	href "alarmMonitoring", title:"SmartThings Alarm Device", description: alarmMonitoringDescription()
        }
        
        section() {
        	href "notifications", title:"SmartThings Notifications", description: notificationsDescription()
        }
    }
}

def awsAPISettingsDescription(){
	if(pauseIntegration)
    	return "Paused, tab to Restart"
    else if (apiurl && apikey && pollInterval)
    	return "Tap to view details"
    else 
    	return "Tap to Configure"
}

def alarmMonitoringDescription(){
	if(alarmsystem)
    	return "Tap to view details"
    else 
    	return "Tap to Configure"	
}

def ringAPISettingsDescription(){
	if((twoFactorEnabled && refreshToken) || (!twoFactorEnabled && (username && password)))
    	return "Tap to view details"
    else 
    	return "Tap to Configure"	
}

def notificationsDescription(){
	if(sendPushMessage && phone)
    	return "Tap to view details"
    else 
    	return "Tap to Configure"	
}

def ringApiSettings(){
	dynamicPage(name: "ringApiSettings", title: "Ring API Configuration", install: false, uninstall: true){
       
        section ("Two Factor Authentication") {
            input(name: "twoFactorEnabled", type: "bool", title: "", required: "true", description: "Have you enabled Two Factor Authentication (2FA) in your Ring Account?", submitOnChange: true)
        }
        
        section ("Ring Account Access"){
            if(twoFactorEnabled) {
            	input(name: "bringYourRefreshKey", type: "bool", title: "", required: "true", description: "Do you want to use an existing Ring Account Refresh Key?", submitOnChange: true)
                if(bringYourRefreshKey) {
                	input(name: "refreshToken", type: "paragraph", title: "2FA Refresh Token from command-line appication", required: "true", description: "Ring Alarm Refresh Token from command line application.")
                } else {
                    input(name: "username", type: "text", title: "Username", required: "true", description: "Ring Alarm Username (Email Address)")
                    input(name: "password", type: "password", title: "Password", required: "true", description: "Ring Alarm Password")
                }
            } else {
                input(name: "username", type: "text", title: "Username", required: "true", description: "Ring Alarm Username (Email Address)")
                input(name: "password", type: "password", title: "Password", required: "true", description: "Ring Alarm Password")
            }
        }
        
        section("Two Factor Authentication"){
        	if(twoFactorEnabled && !bringYourRefreshKey && !state.ringRefreshKey) {
        		href "finishRingAccount2FA", title: ringAccountStatusTitle(), description: ringAccountStatusDescription()
            } else {
            	href "ringAccountStatus", title: "Ring Account Status", description: "Tap to view Ring Account Status"
            }
        }
    }
}

def finishRingAccount2FA(){
	dynamicPage(name: "finishRingAccount2FA", title: "Finish Ring API 2FA Setup", install: false, uninstall: true){
        if(twoFactorEnabled && !bringYourRefreshKey && !state.ringRefreshKey) {
       		ringGet2FAToken()
            section ("Finish the 2FA Authentication") {
                input(name: "twoFactorCode", type: "text", title: "2FA Code)", required: "true", description: "OTP received in Registered Cell Phone Number from Ring.")
            }
            section("Account Status"){
            	href "ringAccountStatus", title: "Ring Account Status", description: "Tap to view Ring Account Status"
            }
        }
	}
}

def ringAccountStatus(){
	dynamicPage(name: "ringAccountStatus", title: "Ring Account Status", install: false, uninstall: true){
    	//Ring 2FA Enabled and Bring Your Own Key option is not enabled but the Refresh Key is not present
        if(twoFactorEnabled && !bringYourRefreshKey && !state.ringRefreshKey) {
            def apiResponse = ringSubmit2FAToken()
             if(apiResponse.data) {
             	log.trace "ringAccountStatus() -> Storing refresh data to scope"
                state.ringRefreshKey = apiResponse.data.refresh_token
             }
        } else if(twoFactorEnabled && bringYourRefreshKey){
        	log.trace "ringAccountStatus() -> Storing refresh data to scope"
            state.ringRefreshKey = settings.refreshToken
        }
        log.trace "ringAccountStatus() -> Gettting Ring Meta Data"
        getRingAccountDetails()

        section ("Account Status") {
            section {
                def locationId = state.ringLocationId
                def zId = state.ringZID
                def locationNames = state.locations.ketSet()
                log.trace "ringAccountStatus() -> Locations are : ${locationNames}"
                if (locationId?.trim() && zId?.trim()) {
                    log.trace "ringAccountStatus() -> Assigning default values as it is not existing. Default Location : ${locationNames.first()}"
                    def defaultLocation  = state.locations[locationNames.first()]
                    log.trace "ringAccountStatus() -> Default location : ${defaultLocation}"
                    state.ringLocationId = defaultLocation.id
                    state.ringZID = defaultLocation.zId
                }
                //def locations = [ "Location 1 (hhh)", "Location 2 ()"]
                input(name: "ringLocation", type: "enum", title: "Alarm Location", required: "false", options: locationNames, defaultValue: locationNames[0], submitOnChange: true)
            }

            if(ringLocation) {
                // Check if location changed.
                def selectedLocationName = settings.ringLocation
                def selectedLocation = state.locations[selectedLocationName]
                log.trace "ringAccountStatus() -> Selected location name : ${selectedLocationName}, details: ${selectedLocation}"
                def selectedLocationId = selectedLocation.id
                def selectedZId = selectedLocation.zId
                // Save updated location to state.
                if (!state.ringLocationId.equals(selectedLocationId) || !state.ringZID.equals(selectedZId)) {
                    log.trace "ringAccountStatus() -> Updating location in state based in user choice : ${selectedLocationName}"
                    state.ringLocationId = selectedLocationId
                    state.ringZID = selectedZId
                    // Removed old devices.
                    log.trace "ringAccountStatus() -> Removing OLD child devices."
                    alarmsystem.removeChildDevices()
                    // Adding new Child devices
                    log.trace "ringAccountStatus() -> Adding NEW child devices."
                    addChildDevices
                }        
            }
            // Display data
            def locationId = state.ringLocationId
            def zId = state.ringZID
            if(!state.ringLocationId) {
                locationId = "Not Available, check back after complete setup"
            }
            if(!state.ringZID) {
                zId = "Not Available, check back after complete setup"
            }
            String ringAddtionalData = "Location Id - ${locationId}\n\nZID - ${zId}"
            section {
                paragraph ringAddtionalData
            }
        }

    }
}

def ringAccountStatusTitle(){
	if(state.ringRefreshKey) {
    	return "Account Status"
    } else if(twoFactorEnabled) {
		if(bringYourRefreshKey) {
        	return "Account Status"
        } else {
        	return "Finish 2FA Setup"
        }
    } else {
    	return "Account Status"
    }
}

def ringAccountStatusDescription(){
	if(state.ringRefreshKey) {
    	return "Tap to view Account Status"
    } else if(twoFactorEnabled) {
		if(bringYourRefreshKey) {
        	return "Tap to view Account Status"
        } else {
        	return "Tap to view Finish 2FA Setup"
        }
    } else {
    	return "Tap to view Account Status"
    }
}

def awsAPISettings() {
	dynamicPage(name: "awsAPISettings", title: "SmartThings AWS API Configuration", install: false, uninstall: true) {
    	section("Pause AWS Ring API Integration") {
            input(name: "pauseIntegration", type: "bool", title: "", required: "true", description: "Pause AWS Ring API Integration temporarily for performing updates/maintenance.")
        }
        
        section("Ring Alarm AWS API Settings") {
            input(name: "apiurl", type: "text", title: "API Url", required: "true", description: "Ring Alarm AWS API URL/Endpoint")
            input(name: "apikey", type: "text", title: "API Key", required: "true", description: "Ring Alarm AWS API Key")
        }
        
        section {
            input(name: "pollInterval", type: "enum", title: "Polling Interval", required: "true", options: ["1 minute", "5 minutes", "10 minutes", "15 minutes"], defaultValue: "5")
        }
    }
}

def alarmMonitoring(){
	dynamicPage(name: "alarmMonitoring", title: "Alarm Monitoring", install: false, uninstall: true) {
        section("Use this Alarm...") {
            input "alarmsystem", "capability.alarm", multiple: false, required: true
        }
        section("Set alarm to 'Off' when mode matches") {
            input "modealarmoff", "mode", title: "Select modes for 'Disarmed'", multiple: true, required: false
        }
        section("Set alarm to 'Away' when mode matches") {
            input "modealarmaway", "mode", title: "Select modes for 'Armed Away'", multiple: true, required: false  
        }
        section("Set alarm to 'Home' when mode matches") {
            input "modealarmhome", "mode", title: "Select modes for 'Armed Home'", multiple: true, required: false
        }
    }
}

def notifications() {
    dynamicPage(name: "notifications", title: "Notifications Options", install: true, uninstall: true) {
        section("Notifications") {
            input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required: false
            input "phone", "phone", title: "Send a Text Message?", required: false
        }
        section([mobileOnly:true]) {
            label title: "Assign a name", required: false
        }
    }
}

def installed() {
	init()
}

def updated() {
	log.trace("updated() -> Starting update process")
    unsubscribe()
    log.trace("updated() -> unsubscribed")
    unschedule()
    log.trace("updated() -> unscheduled")
    runIn(5, init)
}
  
def init() {
	log.info "init() -> initializing"
	subscribe(app, onAppTouchHandler)
    log.trace("init() -> subscribe(app, onAppTouch)")
    //subscribe(location, "alarmSystemStatus", shmHandler)
    //log.trace("init() ->  subscribe(location, alarmSystemStatus, modeAction)")
    subscribe(location, "mode", modeHandler)
    log.trace("init() ->  subscribe(location, mode, modeHandler)")
    subscribe(alarmsystem, "alarm", alarmStateHandler)
    log.trace("init() -> subscribe(alarmsystem, alarm, alarmStateHandler)")
    state.ringAlarmStatus = "UNKNOWN"
    runIn(3, getRingAccountDetails)
    runIn(15, addChildDevices)
    runIn(40, scheduleDeviceRefresh)
    runEvery15Minutes(addChildDevices)
}

def alarmStateHandler(evt){
	log.info("alarmStateHandler() -> ${evt.value}")
	setAlarmModeBasedOnDeviceTrigger(evt.value)
}

def onAppTouchHandler(evt) {
	log.debug("onAppTouch() -> Running App Manually ${evt.value}")
    state.locationmode = location.mode
	setalarmmode()
}

def shmHandler(evt) {
	//Possible SHM values are away|stay|off
	log.debug("modeaction() ->  ${location.currentState("alarmSystemStatus").value.toLowerCase()}")
	setAlarmModeBasedOnSHMTrigger(${location.currentState("alarmSystemStatus").value.toLowerCase()})
}

def modeHandler(evt){
	log.debug("modeaction() ->  ${evt.value}")
	state.locationmode = evt.value
	setAlarmModeBasedOnModeTrigger()
}

def setAlarmModeBasedOnSHMTrigger(shmStatus) {
    log.debug("setAlarmModeBasedOnSHMTrigger() -> Setting Ring Alarm mode ${alarmsystem} based on SHM status ${shmStatus}")
    def currentRingAlarmStatus = state.ringAlarmStatus
    log.debug("setAlarmModeBasedOnSHMTrigger() -> Current alarm state is: ${currentRingAlarmStatus}")
    //Possible SHM values are away|stay|off
    switch(shmStatus) {
        case "off" :
            setAlarmOff()
            break
        case "stay" :
            setAlarmHome()
        	break
        case "away" :
            setAlarmAway()
            break
        default:
            log.error "setAlarmModeBasedOnSHMTrigger() -> Unsupported Status - ${shmStatus}"
        	break
    } 
}

def setAlarmModeBasedOnDeviceTrigger(ringAlarmStatus) {
    log.debug("setAlarmModeBasedOnDeviceTrigger() -> Setting Ring Alarm mode ${alarmsystem} to ${ringAlarmStatus}")
    def currentRingAlarmStatus = state.ringAlarmStatus
    log.debug("setAlarmModeBasedOnDeviceTrigger() -> Current alarm state is: ${currentRingAlarmStatus}")
    if(currentRingAlarmStatus != ringAlarmStatus) {
    	switch(ringAlarmStatus) {
            case "off" :
            	setAlarmOff()
                break
            case "home" :
            	setAlarmHome()
                break
            case "away" :
            	setAlarmAway()
                break
            default:
                log.error "setAlarmModeBasedOnDeviceTrigger() -> Unsupported Status - ${setalarmhome}"
                break
        }
    }
}

def setAlarmModeBasedOnModeTrigger() {
    log.debug("setAlarmModeBasedOnModeTrigger() -> Setting Ring Alarm mode ${alarmsystem}")
	state.alarmstate = alarmsystem.currentState("alarm").value.toLowerCase()
    log.debug("setAlarmModeBasedOnModeTrigger() -> Current alarm state is: ${state.alarmstate}")
	if(state.locationmode in modealarmoff && state.alarmstate !="off") {
    	log.debug("setAlarmModeBasedOnModeTrigger() -> Location mode: $state.locationmode")
    	setAlarmOff()
    } else if(state.locationmode in modealarmaway && state.alarmstate !="away") {
		log.debug("setAlarmModeBasedOnModeTrigger() -> Location mode: $state.locationmode")
    	setAlarmAway()
  	} else if(state.locationmode in modealarmhome && state.alarmstate !="home") {
		log.debug("setAlarmModeBasedOnModeTrigger() -> Location mode: $state.locationmode")
        setAlarmHome()
	} else {
		log.debug("setAlarmModeBasedOnModeTrigger() -> No actions set for location mode ${state.locationmode} or ${alarmsystem.displayName} already set to ${state.alarmstate} - aborting")
	}
}

def getRingAccountDetails() {
	if(state.ringLocationId && state.ringZID) {
    	log.trace("getRingAccountDetails() -> LocationId and ZID is present in the state, skipping API call.")
    } else {
		log.trace("getRingAccountDetails() -> Either LocationId or ZID is not present in the state, making AWS API call to get the values.")
        def apiResponse = ringApiCall("meta")
        if(apiResponse.data) {
            state.locations = apiResponse.data
            def locationsMap = [:]

            def locations = new groovy.json.JsonSlurper().parseText(apiResponse.data)
            locations.each { 
                log.trace("getRingAccountDetails() -> Location is : $it")
                locationsMap($it.name, $it)
            }
            state.locations = locationsMap
			//state.ringLocationId = apiResponse.data.locationId
			//state.ringZID = apiResponse.data.zId
		}
    }
}

def setAlarmOff() {
    def apiResponse = ringApiCall("off")
    if(apiResponse.data.message) {
        state.ringAlarmStatus = "off"
        def message = "Ring Alarm is DISARMED"
        log.info("setalarmoff() -> ${message}")
        send(message)
        alarmsystem.off()
    }
}
  
def setAlarmAway() {
	def apiResponse = ringApiCall("away")
    if(apiResponse.data.message) {
        state.ringAlarmStatus = "away"
        def message = "Ring Alarm is Armed AWAY"
        log.info("setalarmaway() -> ${message}")
        send(message)
        alarmsystem.away()
    }
}
  
def setAlarmHome() {
	def apiResponse = ringApiCall("home")
    if(apiResponse.data.message) {
       	state.ringAlarmStatus = "home"
        def message = "Ring Alarm is Armed HOME"
        log.info("setalarmhome() -> ${message}")
        send(message)
        alarmsystem.home()
    }
}

  
private send(msg) {
	if (sendPushMessage != "No") {
		log.debug("send() -> sending push message")
		sendPush(msg)
	}
	if (phone) {
		log.debug("send() -> sending text message")
		sendSms(phone, msg)
	}
    
	log.debug msg
}

def readyToMakeAPICall(route){
	if (!settings.apiurl || !settings.apikey) {
    	log.info "readyToMakeAPICall() -> Preferences not configured yet, apiurl, key and credentials needed."
        return false
    }
    
    if (route && route != "meta" && !alarmsystem) {
    	log.error "readyToMakeAPICall() -> No Alarm System Configured, Setup your alarm system using the `Alarm Monitoring` section"
        return false
    }
    
    if (settings.pauseIntegration) {
    	log.info "readyToMakeAPICall() -> Skipping the Ring API calls as the Device is in Pause state for Maintanence. Turn Off `Pause Integration` status to enable API calls."
        retun false
    } 
    
    return true
}

def addChildDevices() {
    def apiResponse = ringApiCall("status")
    if(apiResponse.data) {
        log.info "addChildDevices() -> Creating/Updating Devices"
        alarmsystem.createChildDevices(apiResponse.data)
    }
}

def scheduleDeviceRefresh() {
    log.info "scheduleDeviceRefresh() -> Setting up polling for Device Status Update (every ${settings.pollInterval})..."
    switch(settings.pollInterval) {
    	case "1 minute" :
        	runEvery1Minute(updateChildDeviceStatus)
            break
        case "5 minutes" :
        	runEvery5Minutes(updateChildDeviceStatus)
            break
        case "10 minutes" :
        	runEvery10Minutes(updateChildDeviceStatus)
            break
        case "15 minutes" :
        	runEvery15Minutes(updateChildDeviceStatus)
            break
     	default:
            runEvery5Minutes(updateChildDeviceStatus)
            break
    }
}

def updateChildDeviceStatus() {
	log.debug "updateChildDeviceStatus() -> Updating Child Device Status"
    def apiResponse = ringApiCall("status")
    if(apiResponse.data && settings.pollInterval) {
        alarmsystem.refreshDeviceStatus(apiResponse.data.deviceStatus, settings.pollInterval)
        alarmsystem.updateEventData(apiResponse.data.events)
    }
}

def ringGet2FAToken(){
	if (!settings.username || !settings.password) {
    	log.info "ringGet2FAToken() -> Preferences not for Ring Account Email and/or Password."
        return null
    }
    
	def params = [
        uri: "https://oauth.ring.com/oauth/token",
        body: [
            username: settings.username,
            password: settings.password,
            client_id: "ring_official_ios",
            scope: "client",
            grant_type: "password"
        ]
    ]
    try {
        httpPostJson(params) { apiResponse ->
            log.trace "ringGet2FAToken() -> Ring Alarm `https://oauth.ring.com/oauth/token` response data: ${apiResponse.data}"
            return apiResponse
        }
    } catch (e) {
        log.error "ringGet2FAToken() -> Unable to complete the Ring API Call: $e"
        return null
    }
}

def ringSubmit2FAToken(route){
	if (!settings.username || !settings.password || !settings.twoFactorCode) {
    	log.info "ringSubmit2FAToken() -> Preferences not for Ring Account Email, Password or 2FACode."
        return null
    }
	
    def params = [
        uri: "https://oauth.ring.com/oauth/token",
        headers: [
            'Content-Type':"application/json",
            '2fa-support': true,
            '2fa-code': settings.twoFactorCode
        ],
        body: [
            username: settings.username,
            password: settings.password,
            client_id: "ring_official_ios",
            scope: "client",
            grant_type: "password"
        ]
    ]
    try {
        httpPostJson(params) { apiResponse ->
            log.trace "ringSubmit2FAToken() -> Ring Alarm https://oauth.ring.com/oauth/token response data: ${apiResponse.data}"
            return apiResponse
        }
    } catch (e) {
        log.error "ringSubmit2FAToken() -> Unable to complete Ring API Call: $e"
        return null
    }
}

def ringApiCall(route){
	def apiReady = readyToMakeAPICall()
	log.info "ringApiCall() -> Calling AWS API for Ring Alarm with route `${route}`. API Call ready - ${apiReady}" 
    if(!apiReady) {
    	return null
    }
    def params = [
        uri: "${settings.apiurl}/${route}",
        headers: [
            'x-api-key':settings.apikey
        ],
        body: [
            user: settings.username,
            password: settings.password,
            refreshToken: state.ringRefreshKey,
            locationId: state.ringLocationId,
            zid: state.ringZID,
            historyLimit: 10
        ]
    ]
    try {
        httpPostJson(params) { apiResponse ->
            log.trace "ringApiCall() -> Ring Alarm ${route.toUpperCase()} response data: ${apiResponse.data}"
            return apiResponse
        }
    } catch (e) {
        log.error "ringApiCall() -> Unable to complete the SmartThings Ring AWS API Call: $e"
        return null
    }
}
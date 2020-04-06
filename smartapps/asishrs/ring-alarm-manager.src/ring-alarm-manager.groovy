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
        	href "awsAPISettings", 
            title:"AWS API Integration", 
            description: awsAPISettingsDescription(),
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_WebAPI_921042.png"
        }
        
        section(){
        	href "ringApiSettings", 
            title:"Ring Account", 
            description: ringAPISettingsDescription(),
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/ringalarm.png"
        }
        
        section() {
        	href "alarmMonitoring", 
            title:"Alarm Device", 
            description: alarmMonitoringDescription(),
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_homesecurity.png"
        }
        
        section() {
        	href "notifications", 
            title:"Notifications", 
            description: notificationsDescription(),
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_Alarm_2091358.png"
        }
        
        section() {
            paragraph title: "CAUTION: Reset Ring Tokens", 
            	image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_Warning_32380.png",
            	"Once you select the value as Yes, existing tokens will be removed, and you need to set 2FA for your Ring Account.\n\nIMPORTANT: Select the option No before you proceed to avoid accidental token deletion.\n\nThis action is irreversible."
            def resetOptions = [ "No", "Yes"]
            input(name: "resetTokens", type: "enum", title: "Reset", required: "false", options: resetOptions, defaultValue: resetOptions[0], submitOnChange: true)
        }

        if(resetTokens) {
         	def selection = settings.resetTokens
            log.trace "ringApiSettings() -> User selection for Reset Tokens - ${selection}"
            if(selection == "Yes") {
            	log.trace "ringApiSettings() -> Removing Tokens"
                state.remove('ringRefreshKey')
                state.remove('ringAccessKey')
                state.remove('ringZID')
                state.remove('ringLocationId')
            }
        }
        
        section() {
            paragraph title: "About", 
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_about_2508117.png",
            "Ring Alarm Manager connects Ring Account to the SmartThings platform. Read more about at https://github.com/asishrs/smartthings-ringalarmv2"
        	paragraph "Version 3.3.0\n\nRelease Notes:\n- Support for Ring refresh token authentication.\n- Ability to reset Ring Token.\n- Ability to view Location details.\n- Ability to view Tokens.\n- UI Enhancements."
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
	if((twoFactorEnabled && state.ringRefreshKey) || (!twoFactorEnabled && (username && password)))
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
                input(name: "username", type: "text", title: "Username", required: "true", description: "Ring Alarm Username (Email Address)")
                input(name: "password", type: "password", title: "Password", required: "true", description: "Ring Alarm Password")
            } else {
                input(name: "username", type: "text", title: "Username", required: "true", description: "Ring Alarm Username (Email Address)")
                input(name: "password", type: "password", title: "Password", required: "true", description: "Ring Alarm Password")
            }
        }
        
        section("Two Factor Authentication"){
        	if(twoFactorEnabled && !state.ringRefreshKey) {
        		href "finishRingAccount2FA", title: ringAccountStatusTitle(), description: ringAccountStatusDescription()
            } else {
            	href "ringAccountStatus", title: "Ring Account Status", description: "Tap to view Ring Account Status"
            }
        }
    }
}

def finishRingAccount2FA(){
	dynamicPage(name: "finishRingAccount2FA", title: "Finish Ring API 2FA Setup", install: false, uninstall: true){
        if(twoFactorEnabled && !state.ringRefreshKey) {
       		ringRequest2FAToken()
            section ("Finish the 2FA Authentication") {
                input(name: "twoFactorCode", type: "text", title: "2FA Code", required: "true", description: "OTP received in Registered Cell Phone Number from Ring.")
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
        if(twoFactorEnabled && !state.ringRefreshKey) {
            def apiResponse = ringSubmit2FAToken()
             if(apiResponse.data) {
             	log.trace "ringAccountStatus() -> Storing refresh data to scope"
                state.ringRefreshKey = apiResponse.data.refresh_token
                state.ringKeyRefreshTime = getTimeNow()
             }
        } 
        log.trace "ringAccountStatus() -> Gettting Ring Meta Data"
        getRingAccountDetails()

        section () {
            if(!state.ringLocationId) {
            	 paragraph title: "Ring Location", 
                    image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_museumlocation_3243886.png",
                	"Not Available, check back after complete setup"
            } else {
                paragraph title: "Ring Location", 
                    image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_museumlocation_3243886.png",
                    "Name: ${state.ringLocation.name}\n${state.ringLocation.address.street}\n${state.ringLocation.address.city},${state.ringLocation.address.state}\n${state.ringLocation.address.zipcode}\n\n${state.ringMetaRefreshTime}"
            }
            
            if(!state.ringZID) {
            	 paragraph title: "Ring ZID", 
                 	image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_homesecurity.png",
                	"Not Available, check back after complete setup"
            } else {
            	 paragraph title: "Ring ZID", 
                    image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_homesecurity.png",
                    "${state.ringZID}\n\n${state.ringMetaRefreshTime}"
            }
            
            if(!state.ringRefreshKey) {
            	 paragraph title: "Ring Refresh Token", 
                 	image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_keyrefresh_2564457.png",
                	"Not Available, check back after complete setup"
            } else {
            	 paragraph title: "Ring Refresh Token", 
                    image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_keyrefresh_2564457.png",
                    "${state.ringRefreshKey}\n\n${state.ringKeyRefreshTime}"
            }
            
            if(!state.ringAccessKey) {
            	 paragraph title: "Ring Access Token", 
                 	image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_Key_724568.png",
                	"Not Available, check back after complete setup"
            } else {
            	 paragraph title: "Ring Access Token", 
                    image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_Key_724568.png",
                    "${state.ringAccessKey}\n\n${state.ringKeyRefreshTime}"
            }
        }
    }
}

def ringAccountStatusTitle(){
	if(state.ringRefreshKey) {
    	return "Account Status"
    } else if(twoFactorEnabled) {
        return "Finish 2FA Setup"
    } else {
    	return "Account Status"
    }
}

def ringAccountStatusDescription(){
	if(state.ringRefreshKey) {
    	return "Tap to view Account Status"
    } else if(twoFactorEnabled) {
        return "Tap to view Finish 2FA Setup"
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
    state.preventRougeAuthRequest = false
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
        	state.ringLocation = apiResponse.data.location
			state.ringLocationId = apiResponse.data.location.id
			state.ringZID = apiResponse.data.zId
            state.ringMetaRefreshTime = getTimeNow()
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
    
    if (route && route != "meta" && !alarmsystem && state.preventRougeAuthRequest) {
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
    try{
        if(apiResponse.data) {
            log.info "addChildDevices() -> Creating/Updating Devices"
            alarmsystem.createChildDevices(apiResponse.data)
        }
    } catch (e) {
        log.error "addChildDevices() -> Unable to complete addChildDevices : $e"
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

def pollingInterval() {
    switch(settings.pollInterval) {
    	case "1 minute" : 
        	return 1
        case "5 minutes" :
        	return 5
        case "10 minutes" :
        	return 10
        case "15 minutes" :
        	return 15
     	default:
            return 5
    }
}

def getTimeNow() {
    def now = new Date()
    return now.format("MM/dd/yyyy HH:mm:ss zzz")
}

def updateChildDeviceStatus() {
	log.debug "updateChildDeviceStatus() -> Updating Child Device Status"
    def apiResponse = ringApiCall("status")
    try{
        if(apiResponse.data && settings.pollInterval) {
            alarmsystem.refreshDeviceStatus(apiResponse.data.deviceStatus)
            alarmsystem.updateEventData(apiResponse.data.events, pollingInterval())
        }
    } catch (e) {
        log.error "updateChildDeviceStatus() -> Unable to complete refreshDeviceStatus : $e"
    }
}

def ringRequest2FAToken(){
	if (!settings.username || !settings.password) {
    	log.info "ringRequest2FAToken() -> Preferences not for Ring Account Email and/or Password."
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
            log.trace "ringRequest2FAToken() -> Ring Alarm `https://oauth.ring.com/oauth/token` response data: ${apiResponse.data}"
            return apiResponse
        }
    } catch (e) {
        log.error "ringRequest2FAToken() -> Unable to complete the Ring API Call: $e"
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

def ringGetAccessAndRefreshToken(){
	if (!settings.username || !settings.password || !settings.twoFactorCode) {
    	log.info "ringGetAccessAndRefreshToken() -> Preferences not for Ring Account Email, Password or 2FACode."
        return null
    }
	
    def params = [
        uri: "https://oauth.ring.com/oauth/token",
        headers: [
            'Content-Type':"application/json",
        ],
        body: [
            client_id: "ring_official_ios",
            grant_type: "refresh_token",
            refresh_token: state.ringRefreshKey
        ]
    ]
    try {
        httpPostJson(params) { apiResponse ->
            if(apiResponse.data.error) {
                log.trace "ringGetAccessAndRefreshToken() -> Request for Access Token failed. Error [ ${apiResponse.data.error}, ${apiResponse.data.error_description}]"
                // sendPush("Ring Access Token API call failed.  Error [${apiResponse.data.error}, ${apiResponse.data.error_description}]")
                return null
            } else {
                log.trace "ringGetAccessAndRefreshToken() -> Request for Access Token Successful."
                state.ringRefreshKey = apiResponse.data.refresh_token
                state.ringAccessKey = apiResponse.data.access_token
                state.ringKeyRefreshTime = getTimeNow()
                return apiResponse.data.access_token
            }
        }
    } catch (e) {
        log.error "ringGetAccessAndRefreshToken() -> Unable to complete Ring API Call: $e"
        // sendPush("Ring Access Token API call failed with Response - ${e}")
        return null
    }
}

def ringApiCall(route){
    // Get Access and Refresh Key
    def accessKey = ringGetAccessAndRefreshToken()

    if(!accessKey?.trim()) {
        log.error "ringApiCall() -> Access Token is Null/Empty. Aboritng api call for route `${route}`"
        return null
    }

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
            accessToken: accessKey,
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
        state.preventRougeAuthRequest = true
        return null
    }
}

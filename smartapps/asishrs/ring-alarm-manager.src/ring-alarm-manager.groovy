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
	iconUrl: "https://terms-612db.firebaseapp.com/ringalarm/images/ring_logo.png",
	iconX2Url: "https://terms-612db.firebaseapp.com/ringalarm/images/ring_logo.png"
)

preferences {
	page(name: "pageStart")
    page(name: "ringApiSettings") 
    page(name: "finishRingAccount2FA")
    page(name: "ringAccountStatus")
    page(name: "awsAPISettings") 
    page(name: "alarmMonitoring")            
    page(name: "notifications")
    page(name: "advancedSettings")
    page(name: "debugLogs")
    page(name: "removeTokens")
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
        	href "advancedSettings", 
            title:"Advanced Settings", 
            description: advancedSettingsDescription(),
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_Settings_1713433.png"
        }
        
        section() {
            paragraph title: "About", 
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_about_2508117.png",
            "Ring Alarm Manager connects Ring Account to the SmartThings platform. Read more about at https://github.com/asishrs/smartthings-ringalarmv2"
        	paragraph "Version 3.4.0\n\nRelease Notes:\n- Changed the Ring Token Refresh UI.\n- Ability to print Device Logs in SmartThings and AWS lambda logs.\n- Advanced Settings section for Token Refresh and Device Logs."
            href(name: "oldReleaseNotes",
                 required: false,
                 title: "Previous Release Notes",
                 style: "external",
                 url: "https://github.com/asishrs/smartthings/blob/master/RELEASE.md#ring-alarm-release",
                 description: "Tap to view previous release notes.")
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
	if(state.ringRefreshKey)
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

def advancedSettingsDescription(){
	if(state.ringRefreshKey)
    	return "Tap to view the Advanced Settings"
    else 
    	return "Complete the Ring Account Setup to Enable this."	
}

def refreshTokenDescription(){
	if(state.ringRefreshKey)
    	return "Tap to remove Ring Tokens.\nThis will force you to perfrom Ring Account set up.\n\nThis action is irreversible."
    else 
    	return "Complete the Ring Account Setup to Enable this."	
}


def debugLogsDescription(){
	if(state.ringRefreshKey)
    	return "Tap to print Raw Devices logs in AWS CloudWatch Logs."
    else 
    	return "Complete the Ring Account Setup to Enable this."	
}

def advancedSettings(){
    dynamicPage(name: "advancedSettings", title: "Advanced Settings", install: false, uninstall: false){
    	section() {
        	href "removeTokens", 
            title:"Remove Ring Tokens", 
            description: refreshTokenDescription(),
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_Warning_32380.png"
        }
       
        section() {
        	href "debugLogs", 
            title:"Debug Logs", 
            description: debugLogsDescription(),
            image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_log_903479.png"
        }
    }
}

def removeTokens(){
    dynamicPage(name: "removeTokens", title: "Remove Ring Tokens", install: false, uninstall: false){
    	section(){
        	if(!state.ringRefreshKey) {
				paragraph "Looks like the Ring Setup is not complete yet. Finish the setup and comeback." 
            } else {
                log.trace "removeTokens() -> Removing Tokens"
                state.remove('ringRefreshKey')
                state.remove('ringAccessKey')
                state.remove('ringZID')
                state.remove('ringLocationId')
                paragraph "Tokens are cleared. You need to provice Ring Account login deatils again for the SmartThings application to autheticate." 
            }
        }
    }
}

def debugLogs(){
    dynamicPage(name: "debugLogs", title: "Debug Logs", install: false, uninstall: false){
    	section(){
            if(!state.ringRefreshKey) {
				paragraph "Looks like the Ring Setup is not complete yet. Finish the setup and comeback." 
            } else {
                log.trace("debugLogs() -> Calling API to get Device Deatils")
                def apiResponse = ringApiCall("devices")
                try{
                    if(apiResponse.data) {
                    	apiResponse.data.body.each{ key, value -> 
                        	 log.trace "debugLogs() -> Raw Device Response ${key} - ${value}"
                        }
                        paragraph "Device data is successfully logged in the AWS CloudWatch logs.\nCheck that under Cloud Watch > Log groups > <your lambad name>" 
                    } else {
                        paragraph "Something went wrong, check logs in the `Live Logging` section in SmartThings IDE."
                    }
                }catch (e) {
                    log.error "debugLogs() -> Unable to complete call for Raw devices : $e"
                    paragraph "Something went wrong, check logs in the `Live Logging` section in SmartThings IDE."
                }
            }
        }
    }
}

def ringApiSettings(){
	dynamicPage(name: "ringApiSettings", title: "Ring API Configuration", install: false, uninstall: true){
       
       section(){
            href(name: "href2faRequired",
             title: "Ring 2FA Required",
             required: false,
             style: "external",
             url: "https://support.ring.com/hc/en-us/articles/360024818291-Using-Two-Step-Security-Authentication-with-Your-Ring-Products",
             image: "https://terms-612db.firebaseapp.com/ringalarm/images/noun_Warning_2978627.png",
             description: "Ring Two Factor Authentication (2FA) is required for this app to work. If you haven't enabled 2FA on your account yet, tap to view the instructions.")
       }
        
        section ("Ring Account"){
            input(name: "username", type: "text", title: "Username", required: "true", description: "Ring Alarm Username (Email Address)")
            input(name: "password", type: "password", title: "Password", required: "true", description: "Ring Alarm Password")
        }
        
        section("Two Factor Authentication"){
        	if(!state.ringRefreshKey) {
        		href "finishRingAccount2FA", title: ringAccountStatusTitle(), description: ringAccountStatusDescription()
            } else {
            	href "ringAccountStatus", title: "Ring Account Status", description: "Tap to view Ring Account Status"
            }
        }
    }
}

def finishRingAccount2FA(){
	dynamicPage(name: "finishRingAccount2FA", title: "Finish Ring API 2FA Setup", install: false, uninstall: true){
        if(!state.ringRefreshKey) {
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
        if(!state.ringRefreshKey) {
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
    } else {
    	return "Finish 2FA Setup"
    }
}

def ringAccountStatusDescription(){
	if(state.ringRefreshKey) {
    	return "Tap to view Account Status"
    } else {
    	return "Tap to view Finish 2FA Setup"
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

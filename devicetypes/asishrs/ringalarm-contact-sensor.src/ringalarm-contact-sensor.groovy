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
	definition (name: "Ring Contact Sensor", namespace: "asishrs", author: "Asish Soudhamma", cstHandler: true) {
		capability "Contact Sensor"
        capability "Sensor"
		capability "Refresh"
	}

	tiles() {
        standardTile("contact", "device.contact", decoration: "flat", width: 2, height: 2) {
        	state("closed", label:'${name}', icon:"st.contact.contact.closed", backgroundColor:"#00A0DC")
			state("open", label:'${name}', icon:"st.contact.contact.open", backgroundColor:"#e86d13")     
        }
		main(["contact"])
	}
}

def refresh() {
	log.debug("Ring Contact Sensor child refresh - ${device.deviceNetworkId}")
    parent.childRefresh(device.deviceNetworkId)
}

def installed () {
	log.debug("Ring Contact Sensor Installed - parent ${parent}, ${device.deviceNetworkId}")
}

def uninstalled () {
	log.debug("Ring Contact Sensor Uninstalled - parent ${parent}, ${device.deviceNetworkId}")
	//parent.delete()
}
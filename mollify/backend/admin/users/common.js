/**
	Copyright (c) 2008- Samuli Järvelä

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
*/

function permissionModeFormatter(mode, options, obj) {
	switch (mode.toLowerCase()) {
		case 'a': return "Admin";
		case 'rw': return "Read and Write";
		case 'ro': return "Read Only";
		case 'no': return "No Rights";
		default: return "-";
	}
}

function generatePassword() {
	var length = 8;
	var password = '';
	
    for (i = 0; i < length; i++) {
    	while (true) {
	        c = getRandomNumber();
	        if (isValidPasswordChar(c)) break;
		}
        password += String.fromCharCode(c);
    }
    return password;
}

function isValidPasswordChar(c) {
    if (c >= 33 && c <= 47) return false;
    if (c >= 58 && c <= 64) return false;
    if (c >= 91 && c <= 96) return false;
    if (c >= 123 && c <=126) return false;
    return true;
}

function getRandomNumber() {
	return (parseInt(Math.random() * 1000) % 94) + 33;
}
package com.gitHub.hotFix.util

import com.gitHub.hotFix.model.HotFixComponent

class HotFixUtil {
	static void setStringValueIfBlank(HotFixComponent o, String property, String value) {
		if(!o[property]?.trim()) {
			o[property] = value
		}
	}
}

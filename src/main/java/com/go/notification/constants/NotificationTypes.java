package com.go.notification.constants;

public enum NotificationTypes {
	EMAIL {
		@Override
		public String toString() {
			return "EMAIL";
		}
	},
	DESKTOP {
		@Override
		public String toString() {
			return "DESKTOP";
		}
	},
	BELL {
		@Override
		public String toString() {
			return "BELL";
		}
	},MBPUSH {
		@Override
		public String toString() {
			return "MBPUSH";
		}
	},PUSH {
		@Override
		public String toString() {
			return "PUSH";
		}
	},
	
}

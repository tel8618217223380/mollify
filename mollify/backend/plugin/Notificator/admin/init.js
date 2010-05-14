function initRegistration() {
	return {
		views: [
			{header:"Notificator", id:'menu-header-notificator', views: [
				{title:"Notifications", id:'menu-notifications-list', "class": "NotificatorListView", "script": "notificator.js"}
			]}
		]
	};
}
function handleCallback(args, dialog) {
	var jqDialog = jQuery('#' + dialog);
	if (args.validationFailed) {
		jqDialog.effect('shake', {
			times : 3
		}, 100);
	} else {
		PF(dialog).hide();
	}
}

function start() {
	PF('statusDialog').show();
}

function stop() {
	PF('statusDialog').hide();
}

function redirectTo(url) {
	var href = url;
	setTimeout(function() {
		window.location.href = href;
	}, 6000);
}

function LoadEvent() {
    this.source = null;
    this.init = function () {
        var eventIcon = document.getElementById("event-icon");
        var eventText = document.getElementById("event-text");

        this.source = new EventSource("/event");
        this.source.addEventListener("message", function (ev) {
            var result = ev.data;
            if (result === "0") {
                eventIcon.removeAttribute("class");
                eventIcon.removeAttribute("style");
                eventText.textContent = "";
            } else if (result === "1") {
                eventIcon.setAttribute("class", "fad fa-fw fa-check-circle");
                eventIcon.setAttribute("style","color: green");
                eventText.textContent = " Congratulations! Your application has been passed.";
            } else if (result === "2") {
                eventIcon.setAttribute("class", "fad fa-fw fa-exclamation-circle");
                eventIcon.setAttribute("style","color: orangered");
                eventText.textContent = " We beg your patient that there should be some further inspections of your application.";
            } else {
                eventIcon.setAttribute("class", "fad fa-fw fa-times-circle");
                eventIcon.setAttribute("style","color: red");
                eventText.textContent = " We are apologizing but your application would not meet our policies now.";
            }
        });

        this.source.onerror = function () {
            stop();
        };

        this.stop = function () {
            this.source.close();
        }
    };
}

eve = new LoadEvent();
window.onload = function () {
    eve.init();
};
window.onbeforeunload = function () {
    eve.stop();
};
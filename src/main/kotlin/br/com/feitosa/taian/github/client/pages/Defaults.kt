package br.com.feitosa.taian.github.client.pages

import kotlinx.html.*

internal fun HEAD.getDefaultHead() {
    script { src = "https://www.gstatic.com/firebasejs/ui/4.5.0/firebase-ui-auth.js" }
    script { src = "https://www.gstatic.com/firebasejs/7.20.0/firebase-app.js" }
    script { src = "https://www.gstatic.com/firebasejs/7.8.0/firebase-auth.js" }
    script { src = "https://www.gstatic.com/firebasejs/7.20.0/firebase-analytics.js" }
    link {
        href = "https://www.gstatic.com/firebasejs/ui/4.5.0/firebase-ui-auth.css"
        rel = "stylesheet"
        type = "text/css"
    }
    script { src = "/static/dist/script.min.js" }
    link {
        href = "/static/dist/style.min.css"
        rel = "stylesheet"
        type = "text/css"
    }
}

package bigchris.studying.accountcreator

class JavascriptStrings {

    companion object {
        val CLICK_LOGIN : String =
            "(function() {Array.prototype.forEach.call(document.getElementsByTagName('a'),(element => {if (element.getAttribute('href') === 'https://www.reddit.com/login') {element.click()}}))})()"

        val ENTER_EMAIL : String = "document.getElementById('desktop-onboarding-email').value = "

        val CHECK_EMAIL_ERROR : String =
            "(function() {var result = true; Array.prototype.forEach.call(document.getElementsByTagName('span'), (element => {if (element.classList.contains('c-form-control-feedback-error') && window.getComputedStyle(element, null).display == 'block') result = false } )); return result})()"

        val EMAIL_SUBMIT : String =
            "Array.prototype.forEach.call(document.getElementsByTagName('button'), (element => {if (element.getAttribute('type') === 'submit' && element.innerText === 'NEXT') element.click() }))"

        val SUBSCRIPTIONS_SUBMIT : String =
            "Array.prototype.forEach.call(document.getElementsByTagName('button'), (element => {if (element.innerText === 'NEXT') console.log(element) }))"

        val ENTER_USERNAME : String = "document.getElementById('user_reg').value = "

        val ENTER_PASSWORD : String = "document.getElementById('passwd_reg').value = "
    }
}
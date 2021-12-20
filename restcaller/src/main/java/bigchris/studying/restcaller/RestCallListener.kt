package bigchris.studying.restcaller

import bigchris.studying.restcaller.response.RestCallResponse

interface RestCallListener {

    fun onIsAuthorized()

    fun onGetNewMessages(messagesResponse: RestCallResponse.NewMessagesRestCallResponse)

}
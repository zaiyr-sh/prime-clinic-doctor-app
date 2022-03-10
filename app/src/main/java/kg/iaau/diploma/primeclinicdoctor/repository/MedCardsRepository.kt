package kg.iaau.diploma.primeclinicdoctor.repository

import kg.iaau.diploma.network.api.ApiMedCard

class MedCardsRepository(
    private val apiMedCard: ApiMedCard
) {

    suspend fun getMedCards() = apiMedCard.getMedCards(page = 0)

}
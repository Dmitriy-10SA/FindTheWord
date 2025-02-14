package com.andef.findtheword.data.mapper

import com.andef.findtheword.data.dto.DefDto
import com.andef.findtheword.data.dto.WordFromAPIDto
import com.andef.findtheword.domain.entities.Def
import com.andef.findtheword.domain.entities.WordFromAPI

object DtoMapper {
    private fun mapDefDtoToDef(defDto: DefDto): Def {
        return Def(defDto.word, defDto.pos)
    }

    fun mapWordFromApiDtoToWordFromApi(wordFromAPIDto: WordFromAPIDto): WordFromAPI {
        return WordFromAPI(listMap(wordFromAPIDto.listWithWord))
    }

    private fun listMap(listWithWord: List<DefDto>): List<Def> {
        return listWithWord.map {
            mapDefDtoToDef(it)
        }
    }
}
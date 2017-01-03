package ru.wutiarn.tg.openpoll

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.vdurmont.emoji.EmojiParser

fun makeInlineKeyboard(pollId: String, variants: Array<String>): InlineKeyboardMarkup {
    val variantsRow = variants
            .mapIndexed { i, s ->
                InlineKeyboardButton(EmojiParser.parseToUnicode(s))
                        .callbackData("v:$pollId:$i")
            }
            .toTypedArray()

    return InlineKeyboardMarkup(variantsRow)
}
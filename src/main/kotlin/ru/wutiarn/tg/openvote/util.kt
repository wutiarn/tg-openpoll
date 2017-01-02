package ru.wutiarn.tg.openvote

import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.vdurmont.emoji.EmojiParser

fun makeInlineKeyboard(pollId: String, variants: Map<String, String>): InlineKeyboardMarkup {
    val variantsRow = variants
            .map {
                InlineKeyboardButton(EmojiParser.parseToUnicode(it.value))
                        .callbackData("v:$pollId:${it.key}")
            }
            .toTypedArray()

    return InlineKeyboardMarkup(variantsRow, arrayOf(
            InlineKeyboardButton("Results")
                    .callbackData("v:$pollId:r"))
    )
}
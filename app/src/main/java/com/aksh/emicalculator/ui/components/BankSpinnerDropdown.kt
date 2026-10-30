package com.aksh.emicalculator.ui.components

import com.aksh.emicalculator.data.ExtraStrings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.IndianBankInfo
import com.aksh.emicalculator.data.MajorIndianBanks
import com.aksh.emicalculator.data.TranslationRepository

@Composable
fun BankSpinnerDropdown(
    selectedBankName: String,
    language: AppLanguage,
    accentColor: Color,
    onBankSelected: (name: String, defaultRate: Double?) -> Unit,
    testTagPrefix: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Check if current name matches one of the known banks or is "Other"
    val isKnownBank = MajorIndianBanks.any { it.name.equals(selectedBankName, ignoreCase = true) }
    var isOtherSelected by remember(selectedBankName) {
        mutableStateOf(!isKnownBank && selectedBankName.isNotBlank())
    }
    var customBankInput by remember(selectedBankName) {
        mutableStateOf(if (isOtherSelected) selectedBankName else "")
    }

    val otherLabel = TranslationRepository.getOtherBankLabel(language)
    val enterNameHint = TranslationRepository.getEnterBankNameHint(language)

    val displayTitle = when {
        isOtherSelected -> if (customBankInput.isNotBlank()) customBankInput else otherLabel
        isKnownBank -> selectedBankName
        selectedBankName.isNotBlank() -> selectedBankName
        else -> MajorIndianBanks.first().name
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Styled Spinner Container
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { expanded = true }
                    .testTag("${testTagPrefix}_spinner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Bank",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Open Bank List",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Spinner Dropdown Items
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .heightIn(max = 380.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .testTag("${testTagPrefix}_dropdown_menu")
            ) {
                MajorIndianBanks.forEach { bank ->
                    val isCurrent = bank.name.equals(selectedBankName, ignoreCase = true) && !isOtherSelected

                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bank.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrent) accentColor else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Avg: ${bank.defaultRate}% p.a.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        onClick = {
                            isOtherSelected = false
                            onBankSelected(bank.name, bank.defaultRate)
                            expanded = false
                        },
                        modifier = Modifier.testTag("${testTagPrefix}_item_${bank.id}")
                    )
                }

                Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Option: "Other" at the bottom
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = otherLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isOtherSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isOtherSelected) accentColor else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (isOtherSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = accentColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        isOtherSelected = true
                        if (customBankInput.isNotBlank()) {
                            onBankSelected(customBankInput, null)
                        } else {
                            onBankSelected("Custom Bank", null)
                        }
                        expanded = false
                    },
                    modifier = Modifier.testTag("${testTagPrefix}_item_other")
                )
            }
        }

        // Custom Bank Name EditText (OutlinedTextField) when "Other" is chosen
        AnimatedVisibility(
            visible = isOtherSelected,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customBankInput,
                    onValueChange = {
                        customBankInput = it
                        onBankSelected(if (it.isNotBlank()) it else "Custom Bank", null)
                    },
                    label = { Text(enterNameHint, fontSize = 12.sp) },
                    placeholder = { Text(TranslationRepository.getString(language, ExtraStrings.BANK_HINT), fontSize = 12.sp) },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("${testTagPrefix}_custom_bank_input")
                )
            }
        }
    }
}

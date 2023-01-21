package com.grayseal.bookshelf.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.common.io.Resources.getResource
import com.grayseal.bookshelf.R
import com.grayseal.bookshelf.ui.theme.*
import com.grayseal.bookshelf.utils.rememberFirebaseAuthLauncher

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    InputField(
        modifier = modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onAction = onAction
    )
}

@Composable
fun PasswordInput(
    modifier: Modifier,
    passwordState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    val visualTransformation = if (passwordVisibility.value) VisualTransformation.None else
        PasswordVisualTransformation()
    Card(
        modifier = Modifier.padding(bottom = 10.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 20.dp
    ) {
        var color by remember{
            mutableStateOf(Gray500)
        }
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = {
                color = Pink500
                passwordState.value = it
                            },
            placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Lock Icon",
                    tint = color
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = poppinsFamily,
                color = MaterialTheme.colors.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            visualTransformation = visualTransformation,
            trailingIcon = {
                PasswordVisibility(passwordVisibility = passwordVisibility)
            },
            keyboardActions = onAction,
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Yellow,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value = !visible }) {
        if (visible) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = "Visibility Icon",
                tint = Gray500
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.VisibilityOff,
                contentDescription = "Visibility Icon",
                tint = Gray500
            )
        }
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
) {
    Card(
        modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 20.dp
    ) {
        var color by remember{
            mutableStateOf(Gray500)
        }
        OutlinedTextField(
            value = valueState.value,
            onValueChange = {
            color = Pink500
                valueState.value = it
                            },
            placeholder = { Text(text = labelId, fontFamily = poppinsFamily, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AlternateEmail,
                    contentDescription = "Email Icon",
                    tint = color
                )
            },
            singleLine = isSingleLine,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = poppinsFamily,
                color = MaterialTheme.colors.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth(),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Yellow,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SubmitButton(textId: String, loading: Boolean, validInputs: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 10.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        androidx.compose.material3.Button(
            onClick = onClick, modifier = Modifier
                .fillMaxWidth(),
            enabled = !loading && validInputs, shape = RoundedCornerShape(10.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Pink500
            )
        ) {
            if (loading) CircularProgressIndicator(modifier = Modifier.size(25.dp)) else
                if (validInputs) {
                    Text(
                        text = textId,
                        fontFamily = poppinsFamily,
                        modifier = Modifier.padding(5.dp),
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = textId,
                        fontFamily = poppinsFamily,
                        modifier = Modifier.padding(5.dp),
                        fontSize = 15.sp,
                        color = Gray500,
                        fontWeight = FontWeight.SemiBold
                    )
                }
        }
    }
}

@Composable
fun ContinueGoogle(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.google),
        contentDescription = "Google Icon",
        modifier = Modifier.height(28.dp).clickable(onClick = onClick)
    )
}

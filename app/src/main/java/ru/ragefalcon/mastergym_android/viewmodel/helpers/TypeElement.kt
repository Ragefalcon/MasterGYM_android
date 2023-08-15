package ru.ragefalcon.mastergym_android.viewmodel.helpers

enum class TypeElement(
    val label: String,
) {
    Client("BaseClient"),
    Training("BaseTraining"),
    Complex("BaseComplex"),
    RequestComplex("BaseRequestComplex");
}
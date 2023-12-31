

# MasterGYM for Android (alpha)

![Image](https://mastergym.online/MasterGYMIcon192.png)

![Static Badge](https://img.shields.io/badge/made_by-Ragefalcon-orange)


<h2><a  href="https://mastergym.online">Основной проект (Web)</a></h2>
Основной сайт нашей онлайн платформы для фитнеса. Здесь можно 
зарегистрироваться в качестве тренера или клиента. 

> В качестве тренера 
можете попробовать создать комплексы тренировок в разделе "Тренерская", 
а также можете завести своих клиентов в наше приложение и составлять тренировки 
для них, для офлайн клиентов можете использовать наше приложение 
в качестве дневника. 

>Если вы клиент и у вас нет знакомого тренера на нашей платформе, можете 
посетить <a  href="https://mastergym.online?idt=VxtfjIX8S8Q1SLsG49c2"> страницу тестового тренера</a>, персональных тренировок 
он вам не сделает, зато можно воспользоваться бесплатным комплексом который 
представлен на этой страничке, чтобы посмотреть как выглядят 
онлайн-тренировки в нашем проекте.

<h2><a  href="https://FitConstructorImg.b-cdn.net/other/File%20for%20download/MasterGYM_1b.apk" download>
Установочный APK
</a></h2>
Для установки ознакомительной версии нужно будет разрешить установку приложений из
неизвестных источников. 

В текущей версии частично реализован функционал для клиентов фитнес тренеров.
Для того чтобы записаться на персональные тренировки или получить/купить 
комплекс готовых тренировок, на данный момент придется воспользоваться web-версией. 
Но если ваш тренер уже отправляет вам тренировки в нашем приложении или у вас уже есть комплекс тренировок, то их можно
посмотреть и выполнить через эту Android версию. Так же здесь можно будет
отредактировать данные профиля и сменить аватарку.

<table>
  <tr>
    <td>
      <img src="https://FitConstructorImg.b-cdn.net/other/change_avatar_demo.gif" alt="Change Avatar Demo">
    </td>
    <td>
      <img src="https://FitConstructorImg.b-cdn.net/other/open_training_demo_2.gif" alt="Change Avatar Demo">
    </td>
  </tr>
</table>

## Техонологии в проекте

Проект написан на $\normalsize\textsf{\color{orange}Kotlin}$. Интерфейс реализован с помощью 
$\normalsize\textsf{\color{orange}Jetpack Compose}$, 
а взаимодействие с сервером осуществляется с помощью $\normalsize\textsf{\color{orange}Ktor}$.
Сервер так же написан на $\normalsize\textsf{\color{orange}Kotlin}$ 
с использованием $\normalsize\textsf{\color{orange}Spring}$ и 
развернут на серверах Google в $\normalsize\textsf{\color{orange}AppEngine}$.

Для авторизации используется $\normalsize\textsf{\color{orange}Firebase Authentication}$, 
а в качестве базы данных $\normalsize\textsf{\color{orange}Firestore}$.

Видео с упражнениями располагаются в $\normalsize\textsf{\color{orange}CDN}$ bunny.net, что 
позволяет значительно ускорить загрузку.

## Запуск проекта из репозитория

На данный момент исходный код основного проекта (web версии и серверной части) 
находится в закрытых репозиториях. Но если к ним есть доступ или есть своя 
реализация, то, для того чтобы запустить проект из данного репозитория, 
необходимо настроить
свой Firebase проект в котором помимо базы данных и аутентификации нужно будет
<a  href="https://firebase.google.com/docs/android/setup?hl=ru"> добавить 
конфигурацию для Android</a>. В соответствии с инструкцией от Firebase, 
нужно будет загрузить `google-services.json` файл и поместить его в папку `/app`.

Так же нужно задать глобальную переменную с адресом сервера, который отвечает за
основной фукнционал.

```Kotlin 
val serverAdress = "https://адрес_сервера"
```

А в файле `settings.gradle` нужно указать то же имя проекта, 
какое указано в Firebase для Android конфигурации.

```Kotlin 
rootProject.name = "name_android_project_in_firebase"
```


Соответственно если есть доступ к существующему проекту, то эти данные нужно взять из него.


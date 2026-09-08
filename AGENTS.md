# iOS Style Android Keyboard - IME

Проект представляет собой реализацию клавиатуры-IME (Input Method Editor) для Android в стиле iOS, разработанную на Kotlin с использованием Jetpack Compose.

## Архитектура

- **ImeKeyboardService** (`ime/ImeKeyboardService.kt`) — входная точка клавиатуры, расширяет `InputMethodService`. Хостовит `ComposeView` и перехватывает действия из ViewModel для отправки текста в текущее поле ввода через `InputConnection`.
- **KeyboardViewModel** (`ui/KeyboardViewModel.kt`) — хранит состояние клавиатуры (текущая раскладка, Shift, Caps Lock, текст, предложения, высота, тема). Использует `StateFlow` и `SharedFlow` для реактивного UI и обработки действий.
- **KeyboardScreen** (`ui/KeyboardScreen.kt`) — основной экран клавиатуры на Compose. Отображает панель подсказок и сетку клавиш с анимациями перехода раскладки (`AnimatedContent`), градиентом, шумом и `RenderEffect` блюром.
- **KeyboardKey** (`ui/Key.kt`) — composable отдельной клавиши с масштабированием при нажатии (0.9f), всплывающей буквой, внутренним свечением (`drawInnerGlow`), тенью и вибрацией.
- **SuggestionBar** (`ui/SuggestionBar.kt`) — панель подсказок с плавным появлением (`AnimatedVisibility`).
- **Theme** (`ui/theme/Theme.kt`) — Material3 тема с поддержкой светлой и тёмной темы.
- **Model** (`model/`) — data-классы `Key`, `KeyboardLayout`, `KeyRow`, `LayoutType`, провайдер раскладок (`LayoutProvider`), константы кодов клавиш (`KeyEvent`) и sealed class `KeyboardAction` для типизированных действий.

## Основные возможности

- Русская и английская раскладки + цифры/символы
- Переключение языков
- Shift / Caps Lock с анимацией
- Backspace с долгим нажатием (удаляет весь текст)
- Пробел, Enter, язык, цифровая панель
- Всплывающая буква над клавишей
- Пружинящая смена раскладки (`AnimatedContent` + spring)
- Плавная панель подсказок (`AnimatedVisibility`)
- Визуальные эффекты: градиент, шум, тени, внутреннее свечение, RenderEffect блюр
- Вибрация при нажатии
- Тёмная и светлая темы

## Сборка

## Примечания
- Проект использует `androidx.lifecycle:lifecycle-runtime-compose` для `repeatOnLifecycle`.
- `RenderEffect` применяется на API 31+.
- Автокоррекция и динамическая высота клавиатуры реализованы как заглушки и требуют дальнейшей разработки.
- Долгое нажатие Backspace удаляет весь текст.
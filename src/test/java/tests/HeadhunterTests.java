package tests;

import Config.ServiceConfig;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.selenide.AllureSelenide;
import model.UserData;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

@Owner("telepnev")
@Feature("Неавторизованный пользователь")
public class HeadhunterTests extends TestBase {
    UserData userData = new UserData();
    ServiceConfig config = ConfigFactory.newInstance().create(ServiceConfig.class, System.getProperties());
    final String baseUrl = "https://hh.ru/";
    final String registPage = "https://hh.ru/account/signup?backurl=%2Fapplicant%2Fresumes%2Fnew&from=header_new";
    final String vacancy = "QA Automation Engineer";


    @Test
    @DisplayName("Открытие главной страницы")
    @Description("Проверяем открытие главной страницы")
    public void openMainPageTest() {
        step("Открываем главную страницу", () -> {
            open(baseUrl);
            $("h1").shouldHave(text("Работа найдется для каждого"));
        });
    }

    @Test
    @DisplayName("Регистрация нового пользователя")
    @Description("Тест падает из за Капчи")
    public void userRegistrationTest() {
        step("Открываем страницу Регистрации", () -> {
            open(registPage);
            $("h1").shouldHave(text("Регистрация соискателя"));
        });
        step("Вводим Имя", () -> $("[data-qa='account-signup-firstname']").val(userData.getFirstName()));
        step("Вводим Фамилия", () -> $("[data-qa='account-signup-lastname']").val(userData.getLastName()));
        step("Вводим Email", () -> $("[data-qa='account-signup-email']").val(userData.getUserEmail()));
        step("Жмем Зарегистрироваться", () -> $("[data-qa='account-signup-submit']").click());

        step("Вводим Phone", () -> $("[data-qa='resume-phone-cell_phone']").val(userData.getUserPhone()));
        step("Вводим день рождения", () -> $("[data-qa='resume__birthday__day']").val(userData.getUserBirthDay()));
        step("Вводим месяц рождения", () -> $("[data-qa='resume__birthday__month-select']").selectOption(userData.getUserBirthMonth()));
        step("Вводим год рождения", () -> $("[data-qa='resume__birthday__year']").val(userData.getUserBirthYear()));
        step("Выбираем пол Женский", () -> $("[data-qa='resume-gender-female']").click());
        step("Выбираем Гражданство", () -> $("[data-qa='resume-citizenship-control']").val(userData.getUserCitizenship()).pressEnter());
        step("Выбираем Нет опыта работы", () -> $("[data-qa='without-experience']").click());
        step("Сохранить и опубликовать", () -> $("[data-qa='resume-submit']").click());
    }

    @Test
    @DisplayName("Вход авторизованного пользователя")
    @Description("Тест падает из за Капчи")
    public void logInAsAnAuthorizedUser() {
        step("Открываем главную страницу", () ->   open(baseUrl));
            $(byText("Всё верно")).click();
            refresh();
        step("Жмем Войти", () -> $$("[data-qa='login']").find(visible).click());
        step("Вход в личный кабинет", () -> $("body").shouldHave(text("Вход в личный кабинет")));
        step("Заполняем поле Email или телефон", () -> $("[data-qa='login-input-username']").val(config.secretLogin()));
        step("Заполняем поле 'Password'", () -> $("[data-qa='login-input-password']").val(config.secretPassword()));
        step("Войти", () -> $("[data-qa='account-login-submit']").click());
        step("Убеждаемся что пользователь залогинелся", () -> $("a").shouldHave(text("Мои резюме")));
    }

    @Test
    @DisplayName("Выбор региона")
    @Description("Пользователь должен иметь возможность выбрать регион")
    public void selectRegionTest() {
        step("Открываем главную страницу", () -> open(baseUrl));
        step("Устанавливаем локацию г.Москва", () -> {
            $x("//button[@data-qa='mainmenu_areaSwitcher']").click();
            $("#area-search-input").val("Москва").click();
            $x("//span[@data-qa='area-switcher-autocomplete-city']").click();
        });
        step("Проверка смены региона", () -> $(".vacancies-of-the-day-anonymous").shouldHave(text("Вакансии дня в Москве")));
    }

    @Test
    @DisplayName("Поиск вакансии")
    @Description("Пользователь должен иметь возможность найти необходимую вакансию")
    public void searchVacanciesTest() {
        step("Открываем главную страницу", () -> open(baseUrl));
        step("Устанавливаем локацию г.Москва", () -> {
            $x("//button[@data-qa='mainmenu_areaSwitcher']").click();
            $("#area-search-input").val("Москва").click();
            $x("//span[@data-qa='area-switcher-autocomplete-city']").click();
        });
        step("Поиск по вакансии 'QA Automation Engineer'", () -> $x("//*[@data-qa='search-input']").
                val(vacancy)).pressEnter();
        step("Проверяем что вакансии нашлись", () -> $$("[data-qa=vacancy-serp__vacancy]")
                .shouldHave(CollectionCondition.sizeNotEqual(0)));
        step("Переходим в найденую вакансию 'QA Automation Engineer'", () -> $(byText(vacancy)).click());
        step("Проверяем правильность перехода", () -> $($("h1"))
                .shouldHave(text("QA Automation Engineer")));
    }

    @Test
    @DisplayName("Отправка ссылки другому Юзеру")
    @Description("Пользователь должен иметь возможность передать скопированную ссылку")
    public void switchToNewWindowsTest() {
        step("Открываем главную страницу", () -> {
            open("https://hh.ru/vacancy/38506572?query=Qa%20automation%20engineer")
            ;
        });
        step("", () -> {
            switchTo().window(0);
            open("https://hh.ru/vacancy/38506572?query=Qa%20automation%20engineer");
        });
        step("", () -> $("h1").shouldHave(text("QA Automation Engineer")));

    }

}

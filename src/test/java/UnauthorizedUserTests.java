import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

@Owner("telepnev")
@Feature("Неавторизованный пользователь")
public class UnauthorizedUserTests {
    final String baseUrl = "https://hh.ru/";
    final String vacancy = "QA Automation Engineer";

    @BeforeEach
    public void setUp() {
        Configuration.startMaximized = true;
        SelenideLogger.addListener("allure", new AllureSelenide()
                .savePageSource(true)
                .screenshots(true));
    }

    @Test
    @DisplayName("Проверяем открытие главной страницы")
    public void openMainPageTest() {
        step("Открываем главную страницу", () -> {open(baseUrl);
            $("h1").shouldHave(text("Работа найдется для каждого"));});

    }

    @Test
    @DisplayName("Пользователь должен иметь возможность установить регион")
    public void selectRegionTest() {
        step("Открываем главную страницу", () -> open(baseUrl));
        step("Устанавливаем локацию г.Москва", () -> {$x("//button[@data-qa='mainmenu_areaSwitcher']").click();
        $("#area-search-input").val("Москва").click();
        $x("//span[@data-qa='area-switcher-autocomplete-city']").click();});
        step("Проверка смены региона", () -> $(".vacancies-of-the-day-anonymous").shouldHave(text("Вакансии дня в Москве")));
    }

    @Test
    @DisplayName("Пользователь должен иметь возможность найти необходимую вакансию")
    public void searchVacanciesTest() {
        step("Открываем главную страницу", () -> open(baseUrl));
        step("Устанавливаем локацию г.Москва", () -> {$x("//button[@data-qa='mainmenu_areaSwitcher']").click();
            $("#area-search-input").val("Москва").click();
            $x("//span[@data-qa='area-switcher-autocomplete-city']").click();});
        step("Поиск по вакансии 'QA Automation Engineer'", () -> $x("//*[@data-qa='search-input']").
                val(vacancy)).pressEnter();
        step("Проверяем что вакансии нашлись", () -> $$("[data-qa=vacancy-serp__vacancy]")
                .shouldHave(CollectionCondition.sizeNotEqual(0)));
        step("Переходим в найденую вакансию 'QA Automation Engineer'", () -> $(byText(vacancy)).click());
        step("Проверяем правильность перехода", () -> $($("h1"))
                .shouldHave(text("QA Automation Engineer")));

    }

    @Test
    @DisplayName("Пользователь должен иметь возможность передать скопированную ссылку")
    public void switchToNewWindowsTest() {
        step("Открываем главную страницу", () -> {
            open("https://hh.ru/vacancy/38506572?query=Qa%20automation%20engineer")
            ;});
        step("", () -> {switchTo().window(0);
            open("https://hh.ru/vacancy/38506572?query=Qa%20automation%20engineer");});
        step("", () -> $("h1").shouldHave(text("QA Automation Engineer")));

    }
}

package ru.geekbrains.api.loader_api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
class YandexWeatherServiceImplTest {

    @Autowired
    private YandexWeatherServiceImpl yandexWeatherService;

    @ParameterizedTest
    @ValueSource(strings = {"Moscow", "New York", "Berlin"})
    void getDailyWeatherByCityName(String cityName) {
        Optional<ObjectNode> answer = yandexWeatherService.getDailyWeatherByCityName(cityName);
        assertNotNull(answer);
        assert answer.isPresent();
    }

}
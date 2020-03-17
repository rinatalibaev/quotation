package ru.alibaev.quotation;

import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.alibaev.quotation.controller.EnergyLevelController;
import ru.alibaev.quotation.controller.QuotationController;
import ru.alibaev.quotation.enumeration.QuotesEnum;

import java.util.Objects;
import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class QuotationApplicationTests {

	@Autowired
	QuotationController quotationController;

	@Autowired
	EnergyLevelController energyLevelController;

	private MockMvc mockMvc;

	// Перед каждым тестом создаем объект MockMvc (инфраструктуру Spring MVC) для возможности обращаться к подтянутому контроллеру
	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(quotationController).build();
	}


	// Тестируем логику вычисления elvl, RESTы для получения elvl, elvl's
	@SneakyThrows
	@Test
	void testElvlCalculation () {
		// Ожидаем, что elvl будет равным bid, так как elvl для ценной бумаги "RU1111100000" пока что отсутствует
		new TaskAssertEqual(QuotesEnum.MIDDLE, "RU1111100000", QuotesEnum.MIDDLE.getBid()).compute();
		// Ожидаем, что elvl будет равным ask, так как ask < elvl
		new TaskAssertEqual(QuotesEnum.LOW, "RU1111100000", QuotesEnum.LOW.getAsk()).compute();
		// Ожидаем, что elvl будет равным bid, так как bid > elvl
		new TaskAssertEqual(QuotesEnum.HIGH, "RU1111100000", QuotesEnum.HIGH.getBid()).compute();
		// Проверяем, сохранилась ли лучшая цена. Должна быть равна QuotesEnum.HIGH.getBid().
		assertThat(Objects.requireNonNull(energyLevelController.energyLevel("RU1111100000")).getIsin().length() == 12);
		assertThat(Objects.requireNonNull(energyLevelController.energyLevel("RU1111100000")).getElvl().doubleValue() == QuotesEnum.HIGH.getBid());
		// Проверяем, работает ли REST получения всех лучших цен
		mockMvc = MockMvcBuilders.standaloneSetup(energyLevelController).build();
		mockMvc.perform(MockMvcRequestBuilders.get("/energy-level/all")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	// Измеряем и выводим в консоль время, которое необходимо на обработку 100 котировок 4-x видов ценных бумаг
	@Test
	void testProcessingTime () throws InterruptedException {
		long begin = System.currentTimeMillis();
		ForkJoinPool pool = ForkJoinPool.commonPool();
		// Запускаем задачи по сохранению котировок ценных бумаг (4 вида) и вычислению elvl.
		for(int i = 0; i < 10; i++) {
			pool.execute(new Task(QuotesEnum.RANDOM_1.getBid(), QuotesEnum.RANDOM_1.getAsk(), "RU0000000000"));
			pool.execute(new Task(QuotesEnum.RANDOM_2.getBid(), QuotesEnum.RANDOM_2.getAsk(), "RU0000000000"));
			pool.execute(new Task(QuotesEnum.RANDOM_3.getBid(), QuotesEnum.RANDOM_3.getAsk(), "RU0000000000"));
			pool.execute(new Task(QuotesEnum.RANDOM_1.getBid(), QuotesEnum.RANDOM_1.getAsk(), "RU3333333333"));
			pool.execute(new Task(QuotesEnum.RANDOM_2.getBid(), QuotesEnum.RANDOM_2.getAsk(), "RU3333333333"));
			pool.execute(new Task(QuotesEnum.RANDOM_1.getBid(), QuotesEnum.RANDOM_1.getAsk(), "RU4444444444"));
			pool.execute(new Task(QuotesEnum.RANDOM_3.getBid(), QuotesEnum.RANDOM_3.getAsk(), "RU4444444444"));
			pool.execute(new Task(QuotesEnum.RANDOM_1.getBid(), QuotesEnum.RANDOM_1.getAsk(), "RU5555555555"));
			pool.execute(new Task(QuotesEnum.RANDOM_2.getBid(), QuotesEnum.RANDOM_2.getAsk(), "RU5555555555"));
			pool.execute(new Task(QuotesEnum.RANDOM_3.getBid(), QuotesEnum.RANDOM_3.getAsk(), "RU5555555555"));
		}
		pool.shutdown();
		pool.awaitTermination(20, TimeUnit.SECONDS);
		System.out.println("Время обработки 100 котировок по 4-м ценным бумагам: " + (System.currentTimeMillis() - begin));
	}

	// Задача отправки котировки
	class Task extends RecursiveAction {

		private Double bid;
		private Double ask;
		private String isin;

		Task(Double bid, Double ask, String isin) {
			this.bid = bid;
			this.ask = ask;
			this.isin = isin;
		}

		@SneakyThrows
		@Override
		@SuppressWarnings("unchecked")
		public void compute() {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("isin", isin);
			jsonObject.put("bid", bid);
			jsonObject.put("ask", ask);
			String json = jsonObject.toJSONString();
			mockMvc.perform(MockMvcRequestBuilders.post("/quotation")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
					.accept(MediaType.APPLICATION_JSON));
		}
	}

	// Задача для проверки вычисления elvl. Подразумевается, что известно предыдущее значение elvl.
	class TaskAssertEqual extends RecursiveAction {

		private Double bid;
		private Double ask;
		private String isin;
		private Double assertEqual;

		TaskAssertEqual(QuotesEnum quotesEnum, String isin, Double assertEqual) {
			this.bid = quotesEnum.getBid();
			this.ask = quotesEnum.getAsk();
			this.isin = isin;
			this.assertEqual = assertEqual;
		}

		@SneakyThrows
		@Override
		@SuppressWarnings("unchecked")
		public void compute() {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("isin", isin);
			jsonObject.put("bid", bid);
			jsonObject.put("ask", ask);
			String json = jsonObject.toJSONString();
			mockMvc.perform(MockMvcRequestBuilders.post("/quotation")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json)
					.accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk())
					.andExpect(content().string(String.valueOf(assertEqual)));
		}
	}

}

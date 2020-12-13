/* Copyright 2020-2021 Better Ltd (www.better.care)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package care.better.platform.web.template;

import care.better.platform.web.template.context.CompositionBuilderContextKey;
import care.better.platform.web.template.extension.WebTemplateTestExtension;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class RussianBuilderTest extends AbstractWebTemplateTest {

    private ObjectMapper objectMapper;
    private Map context;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        context = ImmutableMap.of(
                CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer");
    }

    @Test
    public void externalTerminology() throws Exception {
        String template = getFileContent("/res/NSI_test_information.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("информация/справочная_информация/из_справочника", "123")
                .put("информация/справочная_информация/из_справочника|value", "Первая категория")
                .put("информация/справочная_информация/time", "2012-12-07T02:46:03")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(retrieve.get("информация/справочная_информация:0/из_справочника|code")).isEqualTo("123");
        assertThat(retrieve.get("информация/справочная_информация:0/из_справочника|value")).isEqualTo("Первая категория");
    }

    @Test
    public void testInvalidOccurences1() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_gynecologist_anamnesis_pregnant .v1.xml");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("прием_пациента/метаданные/идентификатор_специалиста", "ва")
                .put("прием_пациента/метаданные/должность|code", "123")
                .put("прием_пациента/метаданные/должность|value", "Медицинская сестра по физиотерапии")
                .put("прием_пациента/метаданные/специальность|code", "1178")
                .put("прием_пациента/метаданные/специальность|value", "Врач-ортодонт")
                .put("прием_пациента/метаданные/структурное_подразделение_медорганизации_автора", "в")
                .put("прием_пациента/метаданные/название_лпу", "в")
                .put("прием_пациента/метаданные/уникальный_идентификатор", "в")
                .put("прием_пациента/метаданные/код_класса_документа", "в")
                .put("прием_пациента/метаданные/дата_и_время_создания_документа:2013-02-27T00:00", "00")
                .put("прием_пациента/метаданные/вид_медицинской_помощи|code", "at0030")
                .put("прием_пациента/метаданные/основной_диагноз|code", "114118")
                .put("прием_пациента/метаданные/основной_диагноз|value", "T67 - Эффекты воздействия высокой температуры и света")
                .put("прием_пациента/метаданные/клиническая_область|code", "at0039")
                .put("прием_пациента/метаданные/название_документа", "ы")
                .put("прием_пациента/метаданные/комментарии", "ы")
                .put("прием_пациента/социально-бытовые_условия:0/жилищные_условия/тип_жилья|code", "at0006")
                .put("прием_пациента/социально-бытовые_условия:0/тяжелая_физическая_работа", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/гепатит", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/герпес", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/цитомегаловирус", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/ветрянка", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/краснуха", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/корь", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/паротит", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/бесплодие", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/пороки_сердца_без_нарушения_кровообращения", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/пороки_сердца_с_нарушением_кровообращения", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/гипертоническая_болезнь", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/вегето-сосудистая_дистония", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/варикозное_расширение_вен", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/рубец_на_матке", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/гемотрансфузии", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:0/без_особенностей", "true")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:0/непереносимость_лекарственных_средств", "л")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:0/аллергологические_заболевания_в_анамнезе", "л")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:1/без_особенностей", "true")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:1/непереносимость_лекарственных_средств", "г")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:1/аллергологические_заболевания_в_анамнезе", "г")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/профессиональные_вредности", "false")
                .put("прием_пациента/перенесенные_заболевания_и_операции:0/эмоциональные_нагрузки", "false")
                .put("прием_пациента/анамнез_жизни_беременной/получала_препараты_содержащие_актг_и_гормоны_надпочечников", "false")
                .put("прием_пациента/исходы_предыдущих_беременностей:0/аномалия_развития_у_детей", "false")
                .put("прием_пациента/исходы_предыдущих_беременностей:0/неврологические_нарушения_у_детей", "false")
                .put("прием_пациента/метаданные/доступность_документа", "at0019")
                .put("прием_пациента/социально-бытовые_условия:0/бытовые_условия", "at0015")
                .put("прием_пациента/социально-бытовые_условия:0/беременность", "at0018")
                .put("прием_пациента/санитарно-эпидемиологический_анамнез:0/прививки", "at0018")
                .put("прием_пациента/санитарно-эпидемиологический_анамнез:0/находилась_в_районах_опасных_по_эпидемиологической_ситуации", "at0025")
                .put("прием_пациента/санитарно-эпидемиологический_анамнез:0/выезжала_ли_за_границу", "at0027")
                .put("прием_пациента/санитарно-эпидемиологический_анамнез:0/беседа_о_сан.-эпид._режиме", "at0030")
                .put("прием_пациента/санитарно-эпидемиологический_анамнез:0/контакт_с_туберкулезными_больными", "at0033")
                .put("прием_пациента/санитарно-эпидемиологический_анамнез:0/педикулез", "at0036")
                .put("прием_пациента/санитарно-эпидемиологический_анамнез:0/трихофития", "at0039")
                .put("прием_пациента/исходы_предыдущих_беременностей:0/ребенок_родился", "at0015")
                .put("прием_пациента/исходы_предыдущих_беременностей:0/ребенок", "at0019")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(retrieve.get(
                "прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:0/непереносимость_лекарственных_средств")).isEqualTo("л");
        assertThat(retrieve.get(
                "прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:1/непереносимость_лекарственных_средств")).isEqualTo(
                "г");
    }

    @Test
    public void testInvalidOccurences2() throws Exception {
        String template = "openEHR-EHR-COMPOSITION.t_gynecologist_prophylactic_examination.v1.xml";

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("прием_пациента/метаданные/идентификатор_специалиста", "j")
                .put("прием_пациента/метаданные/должность|code", "123")
                .put("прием_пациента/метаданные/должность|value", "Медицинская сестра по физиотерапии")
                .put("прием_пациента/метаданные/специальность|code", "1178")
                .put("прием_пациента/метаданные/специальность|value", "Врач-ортодонт")
                .put("прием_пациента/метаданные/структурное_подразделение_медорганизации_автора", "s")
                .put("прием_пациента/метаданные/название_лпу", "s")
                .put("прием_пациента/метаданные/уникальный_идентификатор", "s")
                .put("прием_пациента/метаданные/код_класса_документа", "s")
                .put("прием_пациента/метаданные/дата_и_время_создания_документа:2013-02-27T00:00", "00")
                .put("прием_пациента/метаданные/вид_медицинской_помощи|code", "at0030")
                .put("прием_пациента/метаданные/основной_диагноз|code", "114118")
                .put("прием_пациента/метаданные/основной_диагноз|value", "T67 - Эффекты воздействия высокой температуры и света")
                .put("прием_пациента/метаданные/клиническая_область|code", "at0039")
                .put("прием_пациента/метаданные/название_документа", "s")
                .put("прием_пациента/метаданные/комментарии", "s")
                .put("прием_пациента/общая_информация/тип_приема|code", "at0066")
                .put("прием_пациента/общий_осмотр/молочные_железы:0/не_изменены", "false")
                .put("прием_пациента/общий_осмотр/молочные_железы:0/нет_отделяемого_из_сосков", "false")
                .put("прием_пациента/общий_осмотр/язык/чистый", "false")
                .put("прием_пациента/общий_осмотр/живот/участвует_в_акте_дыхания", "false")
                .put("прием_пациента/заключение_осмотра/основной_диагноз/основной_диагноз", "false")
                .put("прием_пациента/заключение_осмотра/основной_диагноз/код_по_мкб|code", "114118")
                .put("прием_пациента/заключение_осмотра/основной_диагноз/код_по_мкб|value", "T67 - Эффекты воздействия высокой температуры и света")
                .put("прием_пациента/метаданные/доступность_документа", "at0019")
                .put("прием_пациента/общая_информация/место_приема", "at0067")
                .put("прием_пациента/общая_информация/цель_посещения", "at0071")
                .put("прием_пациента/общий_осмотр/общий_осмотр/общее_состояние", "at0005")
                .put("прием_пациента/общий_осмотр/видимые_кожные_покровы/окраска", "at0080")
                .put("прием_пациента/общий_осмотр/молочные_железы:0/пальпация", "at0011")
                .put("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/размеры", "at0006")
                .put("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:0/локализация", "at0010")
                .put("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:0/консистенция", "at0014")
                .put("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:0/болезненность", "at0016")
                .put("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:1/локализация", "at0009")
                .put("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:1/консистенция", "at0013")
                .put("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:1/болезненность", "at0016")
                .put("прием_пациента/общий_осмотр/язык/влажность", "at0012")
                .put("прием_пациента/общий_осмотр/живот/состояние", "at0175")
                .put("прием_пациента/общий_осмотр/живот/болезненность_при_пальпации", "at0182")
                .put("прием_пациента/общий_осмотр/мочевыводящая_система/мочеиспускание", "at0005")
                .put("прием_пациента/общий_осмотр/мочевыводящая_система/симптом_пастернацкого", "at0024")
                .put("прием_пациента/общий_осмотр/мочевыводящая_система/область_почек", "at0044")
                .put("прием_пациента/гинекологический_осмотр:0/основные_данные/состояние_половой_щели", "at0014")
                .put("прием_пациента/гинекологический_осмотр:0/основные_данные/опущение_стенок_влагалища", "at0017")
                .put("прием_пациента/гинекологический_осмотр:0/шейка_матки:0/слизистая", "at0005")
                .put("прием_пациента/гинекологический_осмотр:0/шейка_матки:0/форма_шейки_матки", "at0012")
                .put("прием_пациента/гинекологический_осмотр:0/шейка_матки:0/зев_шейки_матки", "at0016")
                .put("прием_пациента/гинекологический_осмотр:0/шейка_матки:0/симптом_зрачка", "at0019")
                .put("прием_пациента/гинекологический_осмотр:0/тело_матки:0/положение", "at0007")
                .put("прием_пациента/гинекологический_осмотр:0/тело_матки:0/размеры", "at0011")
                .put("прием_пациента/гинекологический_осмотр:0/тело_матки:0/консистенция", "at0013")
                .put("прием_пациента/гинекологический_осмотр:0/тело_матки:0/болезненность", "at0016")
                .put("прием_пациента/гинекологический_осмотр:0/своды/проходимы", "at0005")
                .put("прием_пациента/гинекологический_осмотр:0/правые_придатки:0/пальпация", "at0019")
                .put("прием_пациента/гинекологический_осмотр:0/правые_придатки:0/болезненность", "at0027")
                .put("прием_пациента/гинекологический_осмотр:0/левые_придатки/пальпация", "at0019")
                .put("прием_пациента/гинекологический_осмотр:0/левые_придатки/болезненность", "at0027")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(retrieve.get("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:0/локализация|code")).isEqualTo(
                "at0010");
        assertThat(retrieve.get("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:1/локализация|code")).isEqualTo(
                "at0009");
    }

    // IBS-54
    @Test
    public void testActivity() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_new_physiatrist_examination.v1.xml");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("прием_пациента/административная_информация/автор_документа", "user")
                .put("прием_пациента/административная_информация/вид_медпомощи|code", "at0024")
                .put("прием_пациента/административная_информация/вид_медпомощи|value", "амбулаторная медицинская помощь")
                .put("прием_пациента/административная_информация/дата_приема", "2013-03-19T00:00:00.000+06:00")
                .put("прием_пациента/административная_информация/документ_создан", "2013-03-19T10:42:52.000+06:00")
                .put("прием_пациента/административная_информация/должность|code", "123")
                .put("прием_пациента/административная_информация/должность|value", "Медицинская сестра по физиотерапии")
                .put("прием_пациента/административная_информация/клиническая_область|code", "at0005")
                .put("прием_пациента/административная_информация/клиническая_область|value", "терапия")
                .put("прием_пациента/административная_информация/медицинское_учреждение|code", "770038")
                .put("прием_пациента/административная_информация/медицинское_учреждение|value", "ГБУЗ ГП № 138 ДЗМ")
                .put("прием_пациента/административная_информация/название_документа",
                     "openEHR-EHR-COMPOSITION.t_new_rheumatologist_examination.v1 2013-03-19T00:00:00")
                .put("прием_пациента/административная_информация/основной_диагноз|code", "114118")
                .put("прием_пациента/административная_информация/основной_диагноз|value", "T67 - Эффекты воздействия высокой температуры и света")
                .put("прием_пациента/административная_информация/отделение", "authorInstitution")
                .put("прием_пациента/административная_информация/специальность|code", "1178")
                .put("прием_пациента/административная_информация/специальность|value", "Врач-ортодонт")
                .put("прием_пациента/административная_информация/дата_приема:2013-03-22T00:00", "00")
                .put("прием_пациента/административная_информация/статус_документа|value", "черновик")
                .put("прием_пациента/административная_информация/статус_документа|code", "at0014")
                .put("прием_пациента/общая_информация/тип_приема|code", "at0026")
                .put("прием_пациента/общая_информация/тип_приема|value", "первичный")
                .put("прием_пациента/общая_информация/место_приема|value", "поликлиника")
                .put("прием_пациента/общая_информация/цель_посещения|value", "заболевание")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/код_по_мкб|code", "114118")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/код_по_мкб|value", "T67 - Эффекты воздействия высокой температуры и света")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/характер_заболевания|value", "острое")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/учитывать_в_листе_уточненных_диагнозов", "false")
                .put("прием_пациента/диагноз_и_результат_обращения/осложнение_основного_диагноза:0/учитывать_в_листе_уточненных_диагнозов", "false")
                .put("прием_пациента/диагноз_и_результат_обращения/сопутствующий_диагноз:0/учитывать_в_листе_уточненных_диагнозов", "false")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/характер_заболевания|code", "at0051")
                .put("прием_пациента/общая_информация/место_приема|code", "at0067")
                .put("прием_пациента/общая_информация/цель_посещения|code", "at0070")
                .put("прием_пациента/процедуры/narrative", "narrative")
                .put("прием_пациента/процедуры/request:0/timing", "R0")
                .put("прием_пациента/процедуры/request:0/название_процедуры|code", "синусные модульные токи")
                .put("прием_пациента/процедуры/request:0/название_процедуры|value", "синусные модульные токи")
                .put("прием_пациента/процедуры/request:0/описание_процедуры", "1")
                .put("прием_пациента/процедуры/request:0/процедуры_-_физиотерапия:0/частота_процедуры|magnitude", "1")
                .put("прием_пациента/процедуры/request:0/процедуры_-_физиотерапия:0/частота_процедуры|unit", "/d")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    // IBS-54
    @Test
    public void testActivityWithProviders() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_new_physiatrist_examination.v1.xml");
        // todo
        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("прием_пациента/административная_информация/автор_документа", "user")
                .put("прием_пациента/административная_информация/вид_медпомощи|code", "at0024")
                .put("прием_пациента/административная_информация/вид_медпомощи|value", "амбулаторная медицинская помощь")
                .put("прием_пациента/административная_информация/дата_приема", "2013-03-19T00:00:00.000+06:00")
                .put("прием_пациента/административная_информация/документ_создан", "2013-03-19T10:42:52.000+06:00")
                .put("прием_пациента/административная_информация/должность|code", "123")
                .put("прием_пациента/административная_информация/должность|value", "Медицинская сестра по физиотерапии")
                .put("прием_пациента/административная_информация/клиническая_область|code", "at0005")
                .put("прием_пациента/административная_информация/клиническая_область|value", "терапия")
                .put("прием_пациента/административная_информация/медицинское_учреждение|code", "770038")
                .put("прием_пациента/административная_информация/медицинское_учреждение|value", "ГБУЗ ГП № 138 ДЗМ")
                .put("прием_пациента/административная_информация/название_документа",
                     "openEHR-EHR-COMPOSITION.t_new_rheumatologist_examination.v1 2013-03-19T00:00:00")
                .put("прием_пациента/административная_информация/основной_диагноз|code", "114118")
                .put("прием_пациента/административная_информация/основной_диагноз|value", "T67 - Эффекты воздействия высокой температуры и света")
                .put("прием_пациента/административная_информация/отделение", "authorInstitution")
                .put("прием_пациента/административная_информация/специальность|code", "1178")
                .put("прием_пациента/административная_информация/специальность|value", "Врач-ортодонт")
                .put("прием_пациента/административная_информация/дата_приема:2013-03-22T00:00", "00")
                .put("прием_пациента/административная_информация/статус_документа|value", "черновик")
                .put("прием_пациента/административная_информация/статус_документа|code", "at0014")
                .put("прием_пациента/общая_информация/тип_приема|code", "at0026")
                .put("прием_пациента/общая_информация/тип_приема|value", "первичный")
                .put("прием_пациента/общая_информация/место_приема|value", "поликлиника")
                .put("прием_пациента/общая_информация/цель_посещения|value", "заболевание")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/код_по_мкб|code", "114118")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/код_по_мкб|value", "T67 - Эффекты воздействия высокой температуры и света")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/характер_заболевания|value", "острое")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/учитывать_в_листе_уточненных_диагнозов", "false")
                .put("прием_пациента/диагноз_и_результат_обращения/осложнение_основного_диагноза:0/учитывать_в_листе_уточненных_диагнозов", "false")
                .put("прием_пациента/диагноз_и_результат_обращения/сопутствующий_диагноз:0/учитывать_в_листе_уточненных_диагнозов", "false")
                .put("прием_пациента/диагноз_и_результат_обращения/основной_диагноз/характер_заболевания|code", "at0051")
                .put("прием_пациента/общая_информация/место_приема|code", "at0067")
                .put("прием_пациента/общая_информация/цель_посещения|code", "at0070")
                .put("прием_пациента/процедуры/request:0/название_процедуры|code", "синусные модульные токи")
                .put("прием_пациента/процедуры/request:0/название_процедуры|value", "синусные модульные токи")
                .put("прием_пациента/процедуры/request:0/описание_процедуры", "1")
                .put("прием_пациента/процедуры/request:0/процедуры_-_физиотерапия:0/частота_процедуры|magnitude", "1")
                .put("прием_пациента/процедуры/request:0/процедуры_-_физиотерапия:0/частота_процедуры|unit", "/d")
                .build();

//        BuilderContext context = new BuilderContext();
//        context.setLanguage("ru");
//        context.setTerritory("RU");
//        context.setComposerName("composer");
//        context.setActivityTimingProvider(id -> {
//            DvParsable parsable = new DvParsable();
//            parsable.setFormalism("timing");
//            parsable.setValue("R0");
//            return parsable;
//        });
//        context.setInstructionNarrativeProvider(id -> ConversionUtils.getText("Description of what instruction is about!"));
//
//        Composition composition = webTemplate.build(values, context);
//        assertThat(composition).isNotNull();
//
//        TemplateValidator validator = getValidator(templateName);
//        assertThat(validator.getValidator().validate(composition)).isEmpty();
    }

    @Test
    public void testIbs60() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_dental_formule.v1.xml");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/название_документа",
                     "Прием пациента врачом-стоматологом-хирургом 23.04.2013")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/дата_приема", "2013-04-26T00:00:00")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/клиническая_область|code", "at0005")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/клиническая_область|value", "терапия")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/автор_документа", "user")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/должность|code", "123")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/должность|value", "Медицинская сестра по физиотерапии")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/специальность|code", "0.1")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/специальность|value",
                     "Врачебные специальности. Лечебное дело. Педиатрия")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/отделение", "Неизвестное отделение")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/медицинское_учреждение|code", "174")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/медицинское_учреждение|value", "174")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/вид_медпомощи|code", "at0024")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/вид_медпомощи|value", "амбулаторная медицинская помощь")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/основной_диагноз|code", "Z04.9")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/основной_диагноз|value", "Обсл. и набл. по неуточ. поводам")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/документ_создан", "2013-04-26T00:00:00")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/статус_документа|code", "at0014")
                .put("прием_пациента_врачом-стоматологом-хирургом/административная_информация/статус_документа|value", "черновик")
                .put("прием_пациента_врачом-стоматологом-хирургом/стоматологический_осмотр/зубная_формула/осмотр_полости_рта_32/a18/состояние_зуба_до_лечения|code",
                     "at0010")
                .put("прием_пациента_врачом-стоматологом-хирургом/стоматологический_осмотр/зубная_формула/осмотр_полости_рта_32/a18/состояние_зуба_до_лечения|value",
                     "П/С")
                .put("прием_пациента_врачом-стоматологом-хирургом/стоматологический_осмотр/зубная_формула/осмотр_полости_рта_16/a48/состояние_зуба_до_лечения|code",
                     "at0009")
                .put("прием_пациента_врачом-стоматологом-хирургом/стоматологический_осмотр/зубная_формула/осмотр_полости_рта_16/a48/состояние_зуба_до_лечения|value",
                     "П")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    // IBS-61
    @Test
    public void testLinks() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_dermatologist_examination.v1.xml");
        // TODO
//        try (InputStream inputStream = RussianBuilderTest.class.getResourceAsStream("/ru/composition_with_Dv_Interval.xml")) {
//            StreamSource source = new StreamSource(inputStream);
//
//            Unmarshaller unmarshaller = JaxbRegistry.getInstance().getUnmarshaller();
//            unmarshaller.setSchema(null);
//
//            JAXBElement<Composition> element = unmarshaller.unmarshal(source, Composition.class);
//
//            Composition composition = element.getValue();
//
//            Link link1 = new Link();
//            DvEhrUri ehrUri1 = new DvEhrUri();
//            ehrUri1.setValue("ehr:///c1");
//            link1.setTarget(ehrUri1);
//            link1.setMeaning(ConversionUtils.getText("follow up"));
//            link1.setType(ConversionUtils.getText("issue"));
//            composition.getLinks().add(link1);
//
//            Link link2 = new Link();
//            DvEhrUri ehrUri2 = new DvEhrUri();
//            ehrUri2.setValue("ehr:///c2");
//            link2.setTarget(ehrUri2);
//            link2.setMeaning(ConversionUtils.getText("follow up2"));
//            link2.setType(ConversionUtils.getText("issue2"));
//            composition.getLinks().add(link2);
//
//            Map<String, Object> retrieve = webTemplate.retrieve(composition);
//
//            assertThat(retrieve).contains(
//                    entry("прием_пациента_врачом-дерматолог/_link:0|meaning", "follow up"),
//                    entry("прием_пациента_врачом-дерматолог/_link:0|type", "issue"),
//                    entry("прием_пациента_врачом-дерматолог/_link:0|target", "ehr:///c1"),
//                    entry("прием_пациента_врачом-дерматолог/_link:1|meaning", "follow up2"),
//                    entry("прием_пациента_врачом-дерматолог/_link:1|type", "issue2"),
//                    entry("прием_пациента_врачом-дерматолог/_link:1|target", "ehr:///c2")
//            );
//        }
    }

    @Test
    public void testIbs72() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_interval_quantity_test.v1.xml");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("test/административная_информация/автор_документа", "User")
                .put("test/административная_информация/вид_медпомощи|code", "at0024")
                .put("test/административная_информация/вид_медпомощи|terminology", "local")
                .put("test/административная_информация/вид_медпомощи|value", "амбулаторная медицинская помощь")
                .put("test/административная_информация/дата_приема", "2013-06-03T00:00:00.000+06:00")
                .put("test/административная_информация/документ_создан", "2013-06-03T13:29:45.000+06:00")
                .put("test/административная_информация/должность|code", "135")
                .put("test/административная_информация/должность|terminology", "NSI")
                .put("test/административная_информация/должность|value", "Средний медицинский персонал: Медицинская сестра по физиотерапии")
                .put("test/административная_информация/клиническая_область|code", "at0005")
                .put("test/административная_информация/клиническая_область|terminology", "local")
                .put("test/административная_информация/клиническая_область|value", "терапия")
                .put("test/административная_информация/медицинское_учреждение|code", "174")
                .put("test/административная_информация/медицинское_учреждение|terminology", "external")
                .put("test/административная_информация/медицинское_учреждение|value", "Городская поликлиника № 67")
                .put("test/административная_информация/название_документа", "test 03.06.2013")
                .put("test/административная_информация/основной_диагноз|code", "A01.3")
                .put("test/административная_информация/основной_диагноз|terminology", "NSI")
                .put("test/административная_информация/основной_диагноз|value", "Паратиф C")
                .put("test/административная_информация/отделение", "Неизвестное отделение")
                .put("test/административная_информация/специальность|code", "0.1")
                .put("test/административная_информация/специальность|terminology", "NSI")
                .put("test/административная_информация/специальность|value", "Врачебные специальности. Лечебное дело. Педиатрия")
                .put("test/административная_информация/статус_документа|code", "at0014")
                .put("test/административная_информация/статус_документа|terminology", "local")
                .put("test/административная_информация/статус_документа|value", "черновик")
                .put("test/interval_quantity/fiels_for_test/upper", "90")
                .put("test/interval_quantity/fiels_for_test/lower", "120")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(retrieve).contains(
                entry("test/interval_quantity/fiels_for_test/lower|magnitude", 120.0));
    }

    @Test
    public void missingInstruction() throws Exception {
        String template = getFileContent("/res/opt referral.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/missingInstruction.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        assertThat(rawComposition.get("content").get(0).get("items").get(0).get("@class").asText()).isEqualTo("INSTRUCTION");
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).hasSize(1);
    }

    @Test
    public void ru239() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_therapist_examination.opt");

        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/therapist_saved.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper
        );

        assertThat(rawComposition.get("content")).hasSize(5);
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> flatComposition2 = objectMapper.readValue(getFileContent("/res/therapist_saved_fixed.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition2),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper
        );

        assertThat(rawComposition2.get("content")).hasSize(6);
        assertThat(rawComposition2.get("content").get(5).get("name").get("value").asText()).isEqualTo("Сведения о выполнении назначения");
        assertThat(getCompositionValidator().validate(template, rawComposition2.toString())).isEmpty();
    }

    @Test
    public void ru431() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_cardiologist_examination.v3.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/cardio.json"));
        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper
        );

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).hasSize(1);

        String template2 = getFileContent("/res/openEHR-EHR-COMPOSITION.t_cardiologist_examination.v3-fix.xml");

        JsonNode rawComposition2 = getCompositionConverter().convertStructuredToRaw(
                template2,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper
        );

        assertThat(getCompositionValidator().validate(template2, rawComposition2.toString())).isEmpty();
    }

    @Test
    public void ru429() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.prevaccinal_examination.opt");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/vaccination.json"));
        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper
        );
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).hasSize(2);
    }

    @Test
    public void ru437() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.vaccination_card.opt");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/ru-composition.json"));

        // todo compare with original
        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper
        );

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(retrieve)
                .contains(entry("карта_профилактических_прививок/туберкулезные_пробы/заготовка_заголовка:0/результат_иммунодиагностики/дата",
                                "2013"));
    }

    @Test
    public void ru462() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/time", "2015-01-01T10:00:00.000+05:00")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", "37.7")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|unit", "°C")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper
        );
        // todo

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(retrieved),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.LOCALE.getKey(), "ru"),
                objectMapper
        );
//         todo check
//        LocaleBasedValueConverter valueConverter = new RussianLocaleValueConverter(new Locale("ru", "RU"));
//        Composition compositionB = webTemplate.build(webTemplate.retrieveFormatted(compositionA, valueConverter), context, valueConverter);

        assertThat(rawComposition1).isNotNull();
    }

    @Test
    public void ru559() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_sociomedical_assessment_referral.v1.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/ru559_composition.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                context,
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        // todo
//        PathValueExtractor extractor = new NameAndNodeMatchingPathValueExtractor(
//                "/content[openEHR-EHR-ADMIN_ENTRY.container_simi.v0,'Сведения о врачебной комиссии']/data[at0001]/items[openEHR-EHR-CLUSTER.health_authorities_simi.v0]/items[at0005]/value");
//        List<Object> values = extractor.getValue(composition);
//        assertThat(values).hasSize(1);
//        assertThat(values.get(0)).isExactlyInstanceOf(DvText.class);
    }

    @Test
    public void ru560() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_outpatient_reference_form_025_1.v4.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/EMIASSIMI-3836-composition.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.LOCALE.getKey(), "ru"),
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
    }

//    private static final class RussianLocaleValueConverter extends LocaleBasedValueConverter {
//        private final Locale locale;
//
//        private RussianLocaleValueConverter(Locale locale) {
//            super(locale);
//
//            this.locale = locale;
//        }
//
//        /**
//         * Форматирование DateTime типов из строки.
//         *
//         * @param value экземпляр класса DateTime.
//         * @return строка DateTime.
//         */
//        @Override
//        public double parseDouble(String value) {
//            try {
//                return NumberFormat.getInstance(locale).parse(value).doubleValue();
//            } catch (Exception ignored) {
//            }
//
//            return Double.parseDouble(value.replace(",", "."));
//        }
//
//        /**
//         * Форматирование строки из double типов.
//         *
//         * @param value экземпляр класса double.
//         * @return строка double.
//         */
//        @Override
//        public String formatDouble(double value) {
//            return NumberFormat.getInstance(locale).format(value);
//        }
//
//        @Override
//        public String formatDateTime(OffsetDateTime dateTime) {
//            return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withLocale(locale).format(dateTime);
//        }
//    }
}

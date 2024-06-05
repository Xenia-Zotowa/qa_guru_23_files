package guru.qa;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import static java.util.Objects.requireNonNull;

public class FilesParsingTest {

    private final ClassLoader cl = FilesParsingTest.class.getClassLoader();

    @DisplayName("Распаковка zip и проверка PDF")
    @Test
    void zipAndPdfFileParsingTest() throws Exception {


        try (ZipInputStream zis = new ZipInputStream(
                requireNonNull(cl.getResourceAsStream("Zips.zip")))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("pdf")) {
                    PDF pdf = new PDF(zis);
                    assertThat(pdf)
                            .containsExactText("JUnit 5 User Guide")
                            .containsExactText("Dependencies");
                }
            }
        }
    }

    @DisplayName("Распаковка zip и проверка xlsx")
    @Test
    void zipAndXlsFileParsingTest() throws Exception {


        try (ZipInputStream zis = new ZipInputStream(
                requireNonNull(cl.getResourceAsStream("Zips.zip")))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("xlsx")) {
                    XLS xls = new XLS(zis);
                    String actualValue = xls.excel.getSheetAt(0).getRow(7).getCell(2).getStringCellValue();

                    Assertions.assertTrue(actualValue.contains("Xls"));
                }
            }
        }
    }

    @DisplayName("Распаковка zip и проверка csv")
    @Test
    void zipAndCsvFileParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                requireNonNull(cl.getResourceAsStream("Zips.zip")))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("csv")) {
                    CSVReader reader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> Arr = reader.readAll();
                    Assertions.assertArrayEquals(new String[]{"Selenide", "https://selenide.org"}, Arr.get(0));
                    Assertions.assertArrayEquals(new String[]{"JUnit 5", "https://junit.org"}, Arr.get(1));
                }
            }
        }
    }

    @DisplayName("Проверка json")
    @Test
    void jsonParsingTest() throws Exception {
        try (InputStream json = cl.getResourceAsStream("tests.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            assertThat(jsonNode).isNotNull();
            assertThat(jsonNode.get("array")).isNotNull();
            assertThat(jsonNode.get("array").isArray()).isTrue();
            assertThat(jsonNode.get("array").size()).isEqualTo(3);
            assertThat(jsonNode.get("boolean").asBoolean()).isTrue();
            assertThat(jsonNode.get("color").asText()).isEqualTo("gold");
            assertThat(jsonNode.get("null").isNull()).isTrue();
            assertThat(jsonNode.get("number").asInt()).isEqualTo(123);
            assertThat(jsonNode.get("object")).isNotNull();
            assertThat(jsonNode.get("object").get("a").asText()).isEqualTo("b");
            assertThat(jsonNode.get("object").get("c").asText()).isEqualTo("d");
            assertThat(jsonNode.get("string").asText()).isEqualTo("Hello World");
        }
    }


}


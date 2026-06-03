package com.skye.aiagentbackend.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "我是帅哥.pdf";
        String content = "我是帅哥";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}

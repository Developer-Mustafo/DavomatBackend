package uz.coder.davomatbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Bu test Spring Boot ilovasining context’ini to‘g‘ri yuklayaptimi
 * tekshiradi. Agar context yuklanmasa, test xato beradi.
 */
@SpringBootTest
class DavomatBackendApplicationTests {

    /**
     * Ilova konteksini yuklash testi.
     * Agar ilova to‘g‘ri ishga tushsa, test muvaffaqiyatli o‘tadi.
     */
    @Test
    void testContextLoads() {
        // Test ichida hech narsa qilinmaydi, faqat context yuklanishi tekshiriladi
    }

}
package rd.pingable.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController("/health")
public class HealthController {
    @GetMapping
    public Map<String, String> health() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("status", "up");
        return hashMap;
    }
}

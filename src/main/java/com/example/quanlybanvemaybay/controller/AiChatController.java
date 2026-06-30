package com.example.quanlybanvemaybay.controller;

import com.example.quanlybanvemaybay.entity.Flight;
import com.example.quanlybanvemaybay.repository.FlightRepository;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final FlightRepository flightRepository;

    public AiChatController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.getOrDefault("message", "").toLowerCase().trim();
        String response = "";

        if (userMessage.contains("rẻ") || userMessage.contains("cheap") || userMessage.contains("thấp nhất")) {
            List<Flight> flights = flightRepository.findAll().stream()
                    .filter(f -> f.getIsCancelled() == null || !f.getIsCancelled())
                    .filter(f -> f.getDepartureTime() != null && f.getDepartureTime().isAfter(java.time.LocalDateTime.now()))
                    .sorted((f1, f2) -> f1.getBasePrice().compareTo(f2.getBasePrice()))
                    .limit(3)
                    .collect(Collectors.toList());

            if (flights.isEmpty()) {
                response = "Hiện tại SkyTravel không có chuyến bay nào khả dụng sắp tới để hiển thị giá rẻ nhất.";
            } else {
                StringBuilder sb = new StringBuilder("Dưới đây là 3 chuyến bay rẻ nhất sắp tới tại SkyTravel:\n\n");
                for (Flight f : flights) {
                    sb.append(String.format("✈️ **%s** (%s ➔ %s)\n" +
                            "   • Khởi hành: %s\n" +
                            "   • Giá vé: **%,.0f đ**\n\n",
                            f.getFlightNumber(),
                            f.getDepartureAirport().getCode(),
                            f.getArrivalAirport().getCode(),
                            f.getDepartureTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            f.getBasePrice().doubleValue()));
                }
                response = sb.toString();
            }
        } else if (userMessage.contains("checkin") || userMessage.contains("check in") || userMessage.contains("thủ tục")) {
            response = "📅 **Quy định Check-in trực tuyến:**\n\n" +
                    "- **Thời gian mở:** Mở trước giờ bay **23 tiếng**.\n" +
                    "- **Thời gian đóng:** Đóng trước giờ bay **1 tiếng**.\n\n" +
                    "Quý khách có thể tự check-in trực tuyến bằng cách vào **Lịch sử đặt vé (Vé của tôi)** và ấn nút **Check-in** khi chuyến bay đến ngày và thời hạn quy định.";
        } else if (userMessage.contains("liên hệ") || userMessage.contains("hotline") || userMessage.contains("sđt") || userMessage.contains("email") || userMessage.contains("trợ giúp")) {
            response = "📞 **Thông tin liên hệ Hỗ trợ khách hàng:**\n\n" +
                    "- **Hotline:** +84 123 456 789 (Hỗ trợ 24/7)\n" +
                    "- **Email:** contact@skytravel.com.vn\n" +
                    "- **Địa chỉ:** 123 Đường Cầu Giấy, Hà Nội, Việt Nam\n\n" +
                    "Nếu quý khách cần giải đáp khẩn cấp, vui lòng gọi điện trực tiếp tới hotline!";
        } else if (userMessage.contains("hủy") || userMessage.contains("hoàn tiền") || userMessage.contains("refund")) {
            response = "❌ **Chính sách Hủy vé & Hoàn tiền:**\n\n" +
                    "- **Vé chưa thanh toán:** Sẽ tự động hủy sau **10 phút** giữ chỗ mà không tính phí.\n" +
                    "- **Vé đã thanh toán:** Quý khách không thể tự hủy trên web. Vui lòng gửi email tới **contact@skytravel.com.vn** hoặc gọi hotline **+84 123 456 789** kèm theo Mã đặt chỗ để nhân viên hỗ trợ xử lý hoàn trả theo quy định.";
        } else if (userMessage.contains("chào") || userMessage.contains("hello") || userMessage.contains("hi") || userMessage.contains("tư vấn")) {
            response = "Xin chào! Tôi là **SkyBot AI** 🤖 - Trợ lý ảo của SkyTravel.\n\n" +
                    "Tôi có thể hỗ trợ bạn tìm kiếm chuyến bay rẻ nhất, hướng dẫn làm thủ tục check-in, hủy vé và cung cấp thông tin liên hệ. Bạn cần tôi giúp gì hôm nay?";
        } else {
            List<Flight> flights = flightRepository.findAll().stream()
                    .filter(f -> f.getIsCancelled() == null || !f.getIsCancelled())
                    .filter(f -> f.getDepartureTime() != null && f.getDepartureTime().isAfter(java.time.LocalDateTime.now()))
                    .limit(3)
                    .collect(Collectors.toList());

            if (flights.isEmpty()) {
                response = "SkyTravel xin chào! Hiện tại hệ thống không có chuyến bay nào sắp khởi hành. Bạn vui lòng quay lại sau.";
            } else {
                StringBuilder sb = new StringBuilder("Tôi chưa hiểu rõ câu hỏi của bạn. Đây là một số chuyến bay sắp tới bạn có thể quan tâm:\n\n");
                for (Flight f : flights) {
                    sb.append(String.format("✈️ **%s** (%s ➔ %s)\n" +
                            "   • Khởi hành: %s\n" +
                            "   • Giá vé: **%,.0f đ**\n\n",
                            f.getFlightNumber(),
                            f.getDepartureAirport().getCode(),
                            f.getArrivalAirport().getCode(),
                            f.getDepartureTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            f.getBasePrice().doubleValue()));
                }
                sb.append("Bạn có thể hỏi tôi về: 'chuyến bay rẻ nhất', 'quy định checkin', 'hủy vé và hoàn tiền', hoặc 'thông tin liên hệ'.");
                response = sb.toString();
            }
        }

        return Map.of("reply", response);
    }
}

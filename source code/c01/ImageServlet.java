package net.java.image;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Servlet implementation class ImageServlet
 */
@WebServlet("/ImageServlet")
@MultipartConfig // Enables handling of multipart/form-data
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public ImageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String path = "./index.html"; // path is relative to webapp root
		request.getRequestDispatcher(path).forward(request, response);
	}
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setHeader("Access-Control-Allow-Origin", "*");
        String zoomOption = request.getParameter("zoomOption");
        String zoomPercentage = request.getParameter("zoomPercentage");
        PrintWriter responseWriter = response.getWriter();
        try (InputStream inputStream = request.getPart("selectedFile").getInputStream()) {
            JmsProducer.sendZoomProperties(zoomPercentage, zoomOption, inputStream);
            response.setStatus(HttpServletResponse.SC_OK);
            responseWriter.write("Image uploaded successfully !");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseWriter.write("Error while processing the image: " + e.getMessage());
        } finally {
            responseWriter.flush();
        }
    }
}

package lk.ijse.dep.web.institute.api;

import lk.ijse.dep.web.institute.AppInitializer;
import lk.ijse.dep.web.institute.business.custom.StudentCourseBO;
import lk.ijse.dep.web.institute.dto.StudentCourseDTO;
import lk.ijse.dep.web.institute.exception.HttpResponseException;
import lk.ijse.dep.web.institute.exception.ResponseExceptionUtil;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : Lucky Prabath <lucky.prabath94@gmail.com>
 * @since : 2021-02-07
 **/
@WebServlet(name = "StudentCourseServlet", urlPatterns = "/api/v1/register/*")
public class StudentCourseServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (Throwable t) {
            ResponseExceptionUtil.handle(t, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jsonb jsonb = JsonbBuilder.create();


        try {
            StudentCourseDTO dto = jsonb.fromJson(req.getReader(), StudentCourseDTO.class);

            if (dto.getStudentId() < 1 || dto.getCourseCode().trim().isEmpty() || dto.getRegisterDate() == null ||
                    dto.getRegisterFee() == null) {
                throw new HttpResponseException(400, "Invalid registration details", null);
            }

            StudentCourseBO studentCourseBO = AppInitializer.getContext().getBean(StudentCourseBO.class);
            studentCourseBO.register(dto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().println(jsonb.toJson(dto));
        } catch (JsonbException exp) {
            throw new HttpResponseException(400, "Failed to read the JSON", exp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

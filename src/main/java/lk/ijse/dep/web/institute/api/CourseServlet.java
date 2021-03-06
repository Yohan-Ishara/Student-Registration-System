package lk.ijse.dep.web.institute.api;

import lk.ijse.dep.web.institute.AppInitializer;
import lk.ijse.dep.web.institute.business.custom.CourseBO;
import lk.ijse.dep.web.institute.dto.CourseDTO;
import lk.ijse.dep.web.institute.exception.HttpResponseException;
import lk.ijse.dep.web.institute.exception.ResponseExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.sql.SQLIntegrityConstraintViolationException;

@WebServlet(name = "CourseServlet",urlPatterns = "/api/v1/courses/*")
public class CourseServlet extends HttpServlet {

    final Logger logger = LoggerFactory.getLogger(CourseServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        }catch (Throwable t){
            ResponseExceptionUtil.handle(t, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jsonb jsonb = JsonbBuilder.create();


        try {
            resp.setContentType("application/json");
            CourseBO courseBO = AppInitializer.getContext().getBean(CourseBO.class);
            resp.getWriter().println(jsonb.toJson(courseBO.getAllCourses()));
        } catch (Throwable t) {
            ResponseExceptionUtil.handle(t, resp);

    }}



    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Jsonb jsonb = JsonbBuilder.create();
        try{
            if(req.getPathInfo() == null || req.getPathInfo().replace("/", "").trim().isEmpty()){
                throw new HttpResponseException(400, "Invalid course id", null);
            }

            String id = req.getPathInfo().replace("/","");
            CourseDTO dto = jsonb.fromJson(req.getReader(), CourseDTO.class);

            if(dto.getCode() != null || dto.getDescription().trim().isEmpty() || dto.getDuration().trim().isEmpty()
                    || dto.getAudience() == null){
                throw  new HttpResponseException(400, "Invalid details", null);
            }

            CourseBO courseBO = AppInitializer.getContext().getBean(CourseBO.class);
            dto.setCode(id);  // correction for update
            courseBO.updateCourse(dto);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }catch (JsonbException exp){
            throw new HttpResponseException(400, "Failed to read the json", exp);
        } catch (Exception e) {
            throw new RuntimeException(e);
    }}

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        try{

            if(req.getPathInfo() == null || req.getPathInfo().replace("/", "").trim().isEmpty()){
                throw new HttpResponseException(400, "Invalid course id", null);
            }

            String id = req.getPathInfo().replace("/", "");

            CourseBO courseBO = AppInitializer.getContext().getBean(CourseBO.class);
            courseBO.deleteCourse(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Jsonb jsonb = JsonbBuilder.create();

        try {
            CourseDTO dto = jsonb.fromJson(req.getReader(), CourseDTO.class);

            if (dto.getCode().trim().isEmpty() || dto.getDescription().trim().isEmpty() || dto.getDuration().trim().isEmpty() ||
                    dto.getAudience() == null) {
                throw new HttpResponseException(400, "Invalid course details", null);
            }

            CourseBO courseBO = AppInitializer.getContext().getBean(CourseBO.class);
            courseBO.saveCourse(dto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().println(jsonb.toJson(dto));
        } catch (SQLIntegrityConstraintViolationException exp) {
            throw new HttpResponseException(400, "Duplicate entry", exp);
        } catch (JsonbException exp) {
            throw new HttpResponseException(400, "Failed to read the json", exp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

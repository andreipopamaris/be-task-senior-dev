package com.amaris.task.controller;

import com.amaris.task.common.response.ResponseModel;
import com.amaris.task.entity.Status;
import com.amaris.task.entity.Task;
import com.amaris.task.exception.TaskManagerErrorCode;
import com.amaris.task.service.CommonServiceOps;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.MediaType;


import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@NoArgsConstructor
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskControllerTest extends CommonServiceOps {

    private static final Logger logger = LogManager.getLogger(TaskControllerTest.class);

    public static final String GET_ALL_TASK_END_POINT = "/task/all";
    public static final String TASK_BY_ID_END_POINT = "/task/";
    public static final String ADD_TASK_END_POINT = "/task/assign";
    public static final String EDIT_TASK_END_POINT = "/task/edit";
    public static final String UNASSIGN_TASK_END_POINT = "/task/unassign";
    public static final String REASSIGN_TASK_END_POINT = "/task/reassign";

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void getAllTask() {

        logger.info(">>>>>>>>>Test case >>>>>> getAllTask>>>>>>" + GET_ALL_TASK_END_POINT);
        webTestClient.get().uri(GET_ALL_TASK_END_POINT)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseModel.class)
                .value((response) -> {
                    assertEquals(4, response.getPayload().size());
                });
    }

    @Test
    public void getTaskById() {
        logger.info(">>>>>>>>>Test case >>>>>> getTaskById>>>>>>" + TASK_BY_ID_END_POINT);
        webTestClient.get().uri(TASK_BY_ID_END_POINT + "1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseModel.class)
                .value(responseModel -> {
                    assertEquals("{id=1, description=Task One, dueDate=27/02/2023, assignee=2, status=ASSIGNED}",
                            responseModel.getPayload().get(0).toString());
                });
    }

    @Test
    public void assignTask() {
        logger.info(">>>>>>>>>Test case >>>>>> assignTask>>>>>>" + ADD_TASK_END_POINT);
        Task task = new Task(4,
                "task one",
                String.valueOf("03/03/2023"),
                2,
                Status.ASSIGNED);

        webTestClient.post().uri(ADD_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=4, description=task one, dueDate=03/03/2023, assignee=2, status=ASSIGNED}", res.getPayload().get(0).toString());
                });
    }

    @Test
    public void unassignTask() {
        logger.info(">>>>>>>>>Test case >>>>>> unassignTask>>>>>>" + UNASSIGN_TASK_END_POINT);
        Task task = new Task(1,
                "unassigned process test",
                String.valueOf("05/03/2023"),
                2,
                Status.UNASSIGNED);

        webTestClient.put().uri(UNASSIGN_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=1, description=unassigned process test, dueDate=05/03/2023, assignee=2, status=UNASSIGNED}", res.getPayload().get(0).toString());
                });
    }

    @Test
    public void reassignTask() {
        logger.info(">>>>>>>>>Test case >>>>>> reassignTask>>>>>>" + REASSIGN_TASK_END_POINT);
        Task task = new Task(1,
                "reassigned process test",
                String.valueOf("05/03/2023"),
                2,
                Status.ASSIGNED);

        webTestClient.put().uri(REASSIGN_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=1, description=reassigned process test, dueDate=05/03/2023, assignee=2, status=ASSIGNED}", res.getPayload().get(0).toString());
                });
    }

    @Test
    public void updateDueDate() {
        logger.info(">>>>>>>>>Test case >>>>>> updateDueDate>>>>>>" + EDIT_TASK_END_POINT);
        Task task = new Task(1,
                "update duedate test",
                String.valueOf("10/03/2023"),
                2,
                Status.ASSIGNED);

        webTestClient.put().uri(EDIT_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals("{id=1, description=update duedate test, dueDate=10/03/2023, assignee=2, status=ASSIGNED}", res.getPayload().get(0).toString());
                });
    }

    @Test
    public void getTaskByIdException() {
        logger.info(">>>>>>>>>Test case >>>>>> getTaskByIdException>>>>>>" + TASK_BY_ID_END_POINT + "18");

        webTestClient.get().uri(TASK_BY_ID_END_POINT + "18")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.NO_TASK_IS_FOUND_BY_ID.getMessage() + 18, res.getErrors().getMessage());
                });
    }

    @Test
    public void assignTaskEmpConstrException() {
        logger.info(">>>>>>>>>Test case >>>>>> assignTaskEmpConstrException>>>>>>" + ADD_TASK_END_POINT);
        Task task = new Task(4,
                "task one",
                String.valueOf("03/03/2023"),
                9,
                Status.ASSIGNED);

        webTestClient.post().uri(ADD_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.REFERENTIAL_INTEGRITY_CONSTRAINT.getMessage() + "9 exist", res.getErrors().getMessage());
                });
    }

    @Test
    public void nullAssigneeException() {
        logger.info(">>>>>>>>>Test case >>>>>> nullAssigneeException>>>>>>" + ADD_TASK_END_POINT);
        Task task = Task.builder()
                .id(4)
                .description("task one")
                .dueDate("03/03/2023")
                .status(Status.ASSIGNED)
                .build();

        webTestClient.post().uri(ADD_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.ASSIGNEE_MUST_NOT_BE_NULL.getMessage(), res.getErrors().getMessage());
                });
    }

    @Test
    public void nullDescriptionException() {

        logger.info(">>>>>>>>>Test case >>>>>> nullDescriptionException>>>>>>" + ADD_TASK_END_POINT);
        Task task = Task.builder()
                .id(4)
                .dueDate("03/03/2023")
                .assignee(2)
                .status(Status.ASSIGNED)
                .build();

        webTestClient.post().uri(ADD_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.DESCRIPTION_MUST_NOT_BE_NULL.getMessage(), res.getErrors().getMessage());
                });
    }

    @Test
    public void nullDueDateException() {

        logger.info(">>>>>>>>>Test case >>>>>> nullDueDateException>>>>>>" + ADD_TASK_END_POINT);
        Task task = Task.builder()
                .id(4)
                .description("task one")
                .assignee(2)
                .status(Status.ASSIGNED)
                .build();

        webTestClient.post().uri(ADD_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.DUE_DATE_MUST_NOT_BE_NULL.getMessage(), res.getErrors().getMessage());
                });
    }

    @Test
    public void nullTaskIdOnUpdateException() {

        logger.info(">>>>>>>>>Test case >>>>>>" + EDIT_TASK_END_POINT);
        Task task = Task.builder()
                .description("task one")
                .dueDate("03/03/2023")
                .assignee(2)
                .status(Status.ASSIGNED)
                .build();

        webTestClient.put().uri(EDIT_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.TASK_ID_IS_MANDATORY.getMessage(), res.getErrors().getMessage());
                });

        logger.info(">>>>>>>>>Test case >>>>>>" + UNASSIGN_TASK_END_POINT);
        webTestClient.put().uri(UNASSIGN_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.TASK_ID_IS_MANDATORY.getMessage(), res.getErrors().getMessage());
                });

        logger.info(">>>>>>>>>Test case >>>>>>" + REASSIGN_TASK_END_POINT);
        webTestClient.put().uri(REASSIGN_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.TASK_ID_IS_MANDATORY.getMessage(), res.getErrors().getMessage());
                });
    }

    @Test
    public void failedToUpdateException() {

        logger.info(">>>>>>>>>Test case >>>>>> failedToUpdateException>>>>>>" + EDIT_TASK_END_POINT);
        Task task = Task.builder()
                .id(14)
                .description("task one")
                .dueDate("03/03/2023")
                .assignee(2)
                .status(Status.ASSIGNED)
                .build();

        webTestClient.put().uri(EDIT_TASK_END_POINT)
                .body(Mono.just(task), Task.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ResponseModel.class)
                .value(res -> {
                    assertEquals(TaskManagerErrorCode.FAILED_TO_UPDATE_TASK.getMessage() + "14 is found", res.getErrors().getMessage());
                });
    }
}

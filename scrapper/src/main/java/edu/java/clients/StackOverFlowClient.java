package edu.java.clients;

import edu.java.dto.stackoverflow.AnswerInfo;
import edu.java.dto.stackoverflow.QuestionInfo;
import edu.java.dto.stackoverflow.StackOverFlowQuestionResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface StackOverFlowClient {

    @GetExchange(url = "/questions/{id}?site=stackoverflow")
    StackOverFlowQuestionResponse<QuestionInfo> getQuestionInfoResponse(@PathVariable long id);

    @GetExchange(url = "/questions/{id}/answers?site=stackoverflow")
    StackOverFlowQuestionResponse<AnswerInfo> getAnswerInfoResponse(@PathVariable long id);

}

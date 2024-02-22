package edu.java.scrapper.clients;

import edu.java.scrapper.dto.stackoverflow.AnswerInfo;
import edu.java.scrapper.dto.stackoverflow.QuestionInfo;
import edu.java.scrapper.dto.stackoverflow.StackOverFlowQuestionResponse;
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

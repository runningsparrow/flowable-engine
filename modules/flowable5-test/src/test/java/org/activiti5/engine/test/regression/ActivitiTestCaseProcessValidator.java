package org.activiti5.engine.test.regression;
import java.util.ArrayList;
import java.util.List;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ValidatorSet;
 
/**
 * Sample Process Validator for Activiti Test case.
 */
public class ActivitiTestCaseProcessValidator implements ProcessValidator {
 
  @Override
  public List<ValidationError> validate(BpmnModel bpmnModel) {
    List<ValidationError> errorList = new ArrayList<ValidationError>();
    CustomParseValidator customParseValidator = new CustomParseValidator();
 
    for (Process process : bpmnModel.getProcesses()) {
      customParseValidator.executeParse(bpmnModel, process);
    }
 
    for (String errorRef : bpmnModel.getErrors().keySet()) {
      ValidationError error = new ValidationError();
      error.setValidatorSetName("Manual BPMN parse validator");
      error.setProblem(errorRef);
      error.setActivityId(bpmnModel.getErrors().get(errorRef));
      errorList.add(error);
    }
    return errorList;
  }
  
  @Override
  public List<ValidatorSet> getValidatorSets() {
    return null;
  }
 
  class CustomParseValidator {
    protected void executeParse(BpmnModel bpmnModel, Process element) {
      for (FlowElement flowElement : element.getFlowElements()) {
        if (!ServiceTask.class.isAssignableFrom(flowElement.getClass())) {
          continue;
        }
        ServiceTask serviceTask = (ServiceTask) flowElement;
        validateAsyncAttribute(serviceTask, bpmnModel, flowElement);
      }
    }
 
    void validateAsyncAttribute(ServiceTask serviceTask, BpmnModel bpmnModel,
        FlowElement flowElement) {
      if (!serviceTask.isAsynchronous()) {
        bpmnModel.addError("Please set value of 'activiti:async'" +
            "attribute as true for task:" + serviceTask.getName(), flowElement.getId());
      }
    }
  }
}
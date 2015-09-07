package roth.lib.service.task;

import roth.lib.annotation.Entity;
import roth.lib.annotation.Property;
import roth.lib.service.HttpServiceRequest;

@Entity
@SuppressWarnings("serial")
public class HttpTaskRequest extends HttpServiceRequest
{
	@Property(name = "taskId")
	protected String taskId;
	
	public HttpTaskRequest()
	{
		
	}
	
	public String getTaskId()
	{
		return taskId;
	}
	
	public HttpTaskRequest setTaskId(String taskId)
	{
		this.taskId = taskId;
		return this;
	}
	
}

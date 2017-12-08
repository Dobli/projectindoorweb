package de.hftstuttgart.projectindoorweb.positionCalculator.internal.utility;

import de.hftstuttgart.projectindoorweb.persistence.entities.Parameter;
import de.hftstuttgart.projectindoorweb.persistence.entities.Project;
import de.hftstuttgart.projectindoorweb.positionCalculator.internal.CorrelationMode;

import java.util.List;

public class ProjectParameterResolver {

    public static Object retrieveParameterValue(Project project, String parameterName, Class<? extends Object> parameterValueClass){

        List<Parameter> projectParameters = project.getProjectParameters();

        double result = 0.0;
        for (Parameter parameter:
             projectParameters) {
            if(parameter.getParameterName().equalsIgnoreCase(parameterName)){
                if(parameterValueClass.isAssignableFrom(Double.class)){
                    return getDoubleValue(parameter.getParameterValue());
                }else if(parameterValueClass.isAssignableFrom(Integer.class)){
                    return getIntValue(parameter.getParameterValue());
                }else if(parameterValueClass.isAssignableFrom(Boolean.class)){
                    return getBooleanValue(parameter.getParameterValue());
                }
            }
        }

        return result;

    }

    private static double getDoubleValue(String parameterValue){

        try{
            return Double.valueOf(parameterValue);
        }catch(NumberFormatException ex){
            ex.printStackTrace();
            return 0.0;
        }

    }

    private static int getIntValue(String parameterValue){


        try{
            return Integer.valueOf(parameterValue);
        }catch(NumberFormatException ex){
            ex.printStackTrace();
            return 0;
        }
    }

    private static boolean getBooleanValue(String parameterValue){

        return Boolean.valueOf(parameterValue);

    }

    private static CorrelationMode getCorrelationMode(String parameterValue){

        switch (parameterValue.toLowerCase()){
            case "euclidian": return CorrelationMode.EUCLIDIAN;
            default: return CorrelationMode.EUCLIDIAN;
            /*
            * Currently (as of December 8th, 2017), our backend knows only the Euclidian correlation mode as
            * this was the only correlation mode implemented in the prototype.
            *
            * */
        }

    }




}

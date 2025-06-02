package mc.ajneb97.utils;

import mc.ajneb97.model.internal.CommonVariable;
import mc.ajneb97.model.internal.VariablesProperties;
import me.clip.placeholderapi.PlaceholderAPI;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariablesUtils {

    private static final Pattern subVariablesPattern = Pattern.compile("\\{([^{}]+)}");


    public static String replaceAllVariablesInLine(String textLine, VariablesProperties variablesProperties){
        StringBuilder newText = new StringBuilder();

        int pos = 0;
        while(pos < textLine.length()){
            char posChar = textLine.charAt(pos);
            if(posChar == '%'){
                int indexLast = textLine.indexOf('%',pos+1);
                if(indexLast != -1){
                    String variable = textLine.substring(pos,indexLast+1);
                    String replacedSubVariables = replaceSubVariables(variable,variablesProperties);
                    String finalReplaced = manageVariableReplacement(replacedSubVariables.substring(1,replacedSubVariables.length()-1),variablesProperties,false);
                    pos = indexLast;
                    if(variable.equals(finalReplaced)){
                        pos--;
                        finalReplaced = finalReplaced.substring(0,finalReplaced.length()-1);
                    }

                    pos++;
                    newText.append(finalReplaced);
                    continue;
                }
            }
            newText.append(posChar);
            pos++;
        }

        return newText.toString();
    }

    private static String replaceSubVariables(String input,VariablesProperties variablesProperties) {
        boolean parseOther = input.contains("parseother_");
        Matcher matcher = subVariablesPattern.matcher(input);

        StringBuffer buffer = new StringBuffer();
        int finds = 0;
        while (matcher.find()) {
            if(parseOther && finds >= 1){
                break;
            }
            String variable = matcher.group(1);
            String replacement = manageVariableReplacement(variable,variablesProperties,true);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));

            finds++;
        }
        matcher.appendTail(buffer);

        String replaced = buffer.toString();
        if (!parseOther && !replaced.equals(input)) {
            return replaceSubVariables(replaced,variablesProperties);
        }
        return replaced;
    }

    private static String manageVariableReplacement(String variable,VariablesProperties variablesProperties,boolean smallVariable){
        //Stored variables
        ArrayList<CommonVariable> storedVariables = variablesProperties.getSavedVariables();
        for(CommonVariable storedVariable : storedVariables){
            if(storedVariable.getValue() != null && variable.equals(storedVariable.getVariable().replace("%",""))){
                return storedVariable.getValue();
            }
        }

        //Global variables
        if(variable.equals("player")){
            // %player%
            return variablesProperties.getPlayer().getName();
        }

        //PlaceholderAPI variables
        if(variablesProperties.isPlaceholderAPI()){
            String variableBefore = variable;
            variable = PlaceholderAPI.setPlaceholders(variablesProperties.getPlayer(),"%"+variable+"%");
            if(!("%"+variableBefore+"%").equals(variable)){
                //Was replaced
                return variable;
            }else{
                variable = variableBefore;
            }
        }

        if(smallVariable){
            return "{"+variable+"}";
        }else{
            return "%"+variable+"%";
        }
    }
}

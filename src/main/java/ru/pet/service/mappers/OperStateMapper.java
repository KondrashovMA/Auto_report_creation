package ru.pet.service.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.pet.model.MMS.BRCB;

import java.util.List;

@Service
public class OperStateMapper {

    @Value("${functions_10}")
    private List<String> functions_10;

    public String getEventDescription(BRCB brcb){
        String desc = "";
        String descText = "Положение выключателя В ";

        if(functions_10.contains(brcb.getVied())){
            descText += "10 кВ ";
        } else descText += "110 кВ ";

        if(brcb.getPath().toLowerCase().contains("pos.stval")){
            switch (brcb.getValue()){
                case "1":
                    desc = descText + brcb.getVied() + " «отключено».";
                    break;
                case "2":
                    desc = descText + brcb.getVied() + " «включено».";
                    break;
                case "0":
                    desc = descText + brcb.getVied() + " «неизвестно».";
                    break;
                case "3":
                    desc = descText + brcb.getVied() + "«промежуточное».";
            }
        }else{
            desc = brcb.getPath() + " в значении " + brcb.getValue();
        }
        return desc;
    }
}

package cn.qaq.valveapi.common;

import lombok.Data;

public enum PokeAnswerEnum {

    ANSWER_6(0,"只有漂亮的小姐姐戳我，我才回哦！"),
    ANSWER_1(1,"别戳啦，再戳我就漏气啦！"),
    ANSWER_2(2,"再戳我就喊非礼啦！"),
    ANSWER_3(3,"再戳我就吃了你！"),
    ANSWER_4(4,"别戳啦！抱抱我！"),
    ANSWER_5(5,"请问找我有什么事吗？");

    private Integer index;
    private String answer;

    PokeAnswerEnum(Integer index , String answer){
        this.index = index;
        this.answer = answer;
    }

    public static String getAnswer(Integer index){
        for(PokeAnswerEnum answerEnum : PokeAnswerEnum.values()){
            if(answerEnum.index == index){
                return answerEnum.answer;
            }
        }
        return null;
    }
}

package cn.qaq.valveapi.common;

public enum ChatWithRobotAnswer {
    ANSWER_6(0,"你们别为难人家啊！"),
    ANSWER_1(1,"我这个小可爱什么都不知道呀！"),
    ANSWER_2(2,"我才刚出生，还需要学习！"),
    ANSWER_3(3,"我不知道呀！");

    private Integer index;
    private String answer;

    ChatWithRobotAnswer(Integer index , String answer){
        this.index = index;
        this.answer = answer;
    }

    public static String getAnswer(Integer index){
        for(ChatWithRobotAnswer answerEnum : ChatWithRobotAnswer.values()){
            if(answerEnum.index == index){
                return answerEnum.answer;
            }
        }
        return null;
    }
}

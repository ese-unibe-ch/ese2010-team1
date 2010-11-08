var questionsList = #{jsAction @Questions.list() /}
var questionsGet = #{jsAction @Questions.get(':id') /}
var voteUp = #{jsAction @Questions.voteUp(':id') /}
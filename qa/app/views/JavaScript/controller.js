var questionsList = #{jsAction @Questions.list() /}
var questionsGet = #{jsAction @Questions.get(':id') /}
var voteUp = #{jsAction @Questions.voteUp(':id') /}
var voteDown = #{jsAction @Questions.voteDown(':id') /}
var recommendedQuestions = #{jsAction @Application.recommendedQuestions(':title') /}
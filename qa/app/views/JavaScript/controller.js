var questionsList = #{jsAction @Questions.list() /}
var questionsGet = #{jsAction @Questions.get(':id') /}
var voteUp = #{jsAction @Questions.voteUp(':id') /}
var voteDown = #{jsAction @Questions.voteDown(':id') /}
var removeVote = #{jsAction @Questions.removeVote(':id') /}
var setBestAnswer = #{jsAction @Questions.setBestAnswer(':id') /}
var resetBestAnswer = #{jsAction @Questions.resetBestAnswer(':id') /}
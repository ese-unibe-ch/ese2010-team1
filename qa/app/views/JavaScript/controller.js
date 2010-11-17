var home = #{jsAction @Questions.home() /}
var showQuestion = #{jsAction @Questions.question(':id') /}
var getQuestion = #{jsAction @Questions.get(':id') /}
var getAnswer = #{jsAction @Questions.entry(':id') /}
var voteUp = #{jsAction @Questions.voteUp(':id') /}
var voteDown = #{jsAction @Questions.voteDown(':id') /}
var removeVote = #{jsAction @Questions.removeVote(':id') /}
var setBestAnswer = #{jsAction @Questions.setBestAnswer(':id') /}
var resetBestAnswer = #{jsAction @Questions.resetBestAnswer(':id') /}
var hotQuestions = #{jsAction @Questions.hot() /}
var myQuestions = #{jsAction @Questions.mine() /}
var activeQuestions = #{jsAction @Questions.active() /}
var searchQuestions = #{jsAction @Questions.search(':string') /}
var questionForm = #{jsAction @Questions.form() /}
var addQuestion = #{jsAction @Questions.add() /}
var answerQuestion = #{jsAction @Questions.answer(':id') /}
var setNotificationAsRed = #{jsAction @Questions.setNotificationAsRed(':id') /}
var contentVersion = #{jsAction @Questions.version(':id') /}

var profileGet = #{jsAction @Users.get(':id', ':theAction') /}
var graphData = #{jsAction @Users.graphData(':id') /}
var editProfile = #{jsAction @Users.saveProfile(':id', ':entrys[]') /}

var login = #{jsAction @Secure.login() /}
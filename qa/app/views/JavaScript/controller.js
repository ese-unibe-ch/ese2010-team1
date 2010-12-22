var home = #{jsAction @Questions.home() /}
var showQuestion = #{jsAction @Questions.question(':id') /}
var getQuestion = #{jsAction @Questions.get(':id') /}
var getAnswer = #{jsAction @Questions.entry(':id') /}
var voteUp = #{jsAction @Questions.voteUp(':id') /}
var voteDown = #{jsAction @Questions.voteDown(':id') /}
var removeVote = #{jsAction @Questions.removeVote(':id') /}
var setBestAnswer = #{jsAction @Questions.setBestAnswer(':id') /}
var resetBestAnswer = #{jsAction @Questions.resetBestAnswer(':id') /}
var hotQuestions = #{jsAction @Questions.hot(':page') /}
var myQuestions = #{jsAction @Questions.mine(':page') /}
var activeQuestions = #{jsAction @Questions.active(':page') /}
var searchQuestions = #{jsAction @Questions.search(':string', ':page') /}
var searchUsers = #{jsAction @Questions.searchUsers(':string') /}
var questionForm = #{jsAction @Questions.form() /}
var addQuestion = #{jsAction @Questions.add() /}
var answerQuestion = #{jsAction @Questions.answer(':id') /}
var setNotificationAsRed = #{jsAction @Questions.setNotificationAsRed(':id') /}
var contentVersion = #{jsAction @Questions.version(':id') /}
var deleteComment = #{jsAction @Questions.deleteComment(':id') /}
var checkUserExists = #{jsAction @Users.checkUserExists(':name') /}
var report = #{jsAction @Questions.report(':id') /}
var like = #{jsAction @Users.likeComment(':id') /}
var unlike = #{jsAction @Users.unlikeComment(':id') /}
var comment = #{jsAction @Questions.comment(':id', ':content') /}

var tagsList = #{jsAction @Questions.getTagList() /};

var profileGet = #{jsAction @Users.get(':id', ':theAction') /}
var graphData = #{jsAction @Users.graphData(':id') /}
var editProfile = #{jsAction @Users.saveProfile(':id', ':entrys[]') /}

var login = #{jsAction @Secure.login() /}

var NUMBER_OF_LOADED_QUESTIONS = ${n};

var token = "${session.current().getAuthenticityToken()}";

var getFraudPointViolations = #{jsAction @Admin.getFraudPointViolations(':id') /}




Create user
- Prompt user_name, password and question / answer
- Generate user_id
- Save user_id, user_name, password (encrypted) in "AUTH_FILE_PATH"
- Save user_id question, answer in "AUTH_QA_FILE_PATH"

Login user
- Prompt user_name and password
- Read from "AUTH_FILE_PATH" and iterate over each line
- Find user_name and match password (encrypt password)
- If user_name and password match, return ask question and then match the answer as well
- Save the login user info in a data structure (User class)


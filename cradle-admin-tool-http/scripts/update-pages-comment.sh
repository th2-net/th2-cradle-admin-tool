#!/bin/bash

# Copyright 2025 Exactpro (Exactpro Systems Limited)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

ARG_CRADLE_ADMIN_TOOL_URL='--cradle-admin-tool-url'
ARG_BOOK='--book'
ARG_START_TIMESTAMP='--start-timestamp'
ARG_END_TIMESTAMP='--end-timestamp'
ARG_COMMENT='--comment'
ARG_MODE='--mode'

MODE_APPEND='append'
MODE_SET='set'
MODE_RESET='reset'
MODE_GET='get'

CRADLE_ADMIN_GET_ALL_BOOKS_PATH='get-all-books'
CRADLE_ADMIN_GET_BOOK_INFO_PATH='get-book-info'
CRADLE_ADMIN_UPDATE_PAGE_PATH='update-page'

CRADLE_ADMIN_BOOK_ID_HTTP_ARG='book-id'
CRADLE_ADMIN_PAGE_NAME_HTTP_ARG='page-name'
CRADLE_ADMIN_NEW_COMMENT_HTTP_ARG='new-page-comment'

CRADLE_ADMIN_BOOK_ID_KEY='bookId'
CRADLE_ADMIN_PAGE_ID_KEY='pageId'
CRADLE_ADMIN_COMMENT_KEY='comment'
CRADLE_ADMIN_PAGES_KEY='pages'
CRADLE_ADMIN_STARTED_KEY='started'
CRADLE_ADMIN_ENDED_KEY='ended'

CRADLE_ADMIN_NULL_VALUE='null'

CRADLE_ADMIN_DEFAULT_COMMENT='auto-page'

TIMESTAMP_REGEX="^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}\:[0-9]{2}\:[0-9]{2}Z$"
REQUIRED_UTILS=('jq' 'curl' 'paste' 'grep' 'head')

print_help() {
  echo 'Help:'
  echo ' Description: this script provide ability to update comment for pages covered by time rage where page.started is included and page.ended is excluded'
  echo " Required utils: ${REQUIRED_UTILS[*]}"
  echo ' Arguments:'
  echo "  ${ARG_MODE} (optional) - work mode. Default value is '${MODE_GET}'"
  echo "     * '${MODE_APPEND}' - appends existed pages' comment by text specified using ${ARG_COMMENT}. '$CRADLE_ADMIN_DEFAULT_COMMENT' default page comment is removed"
  echo "       Final comment has JSON string array format, for example: '[\"<existed comment>\",\"<specified comment>\"]'"
  echo "     * '${MODE_SET}' - sets text specified using ${ARG_COMMENT} as pages' comment"
  echo "       Final comment has JSON string array format, for example: '[\"<specified comment>\"]'"
  echo "     * '${MODE_RESET}' - resets pages' comment to '$CRADLE_ADMIN_DEFAULT_COMMENT' default page comment"
  echo "       Final comment is '$CRADLE_ADMIN_DEFAULT_COMMENT'"
  echo "     * '${MODE_GET}' - prints pages and their comments"
  echo "  ${ARG_CRADLE_ADMIN_TOOL_URL} (required) - cradle admin tool URL"
  echo "  ${ARG_BOOK} (required) - th2 book for searching and updating pages"
  echo "  ${ARG_START_TIMESTAMP} (conditional) - start timestamp for searching page to add ${ARG_COMMENT} comment."
  echo "     - ['${MODE_APPEND}','${MODE_SET}','${MODE_RESET}'] modes (required)"
  echo "     - '${MODE_GET}' mode (optional) - default is min timestamp"
  echo "  ${ARG_END_TIMESTAMP} (conditional) - end timestamp for searching page to add ${ARG_COMMENT} comment."
  echo "     - ['${MODE_APPEND}','${MODE_SET}','${MODE_RESET}'] modes (required)"
  echo "     - '${MODE_GET}' mode (optional) - default is max timestamp"
  echo "  ${ARG_COMMENT} (conditional) - comment for adding to pages found from ${ARG_START_TIMESTAMP} to ${ARG_END_TIMESTAMP}. Required for ['${MODE_APPEND}','${MODE_SET}'] modes"
}

parse_args() {
  CRADLE_ADMIN_TOOL_URL=''
  BOOK=''
  START_TIMESTAMP=''
  END_TIMESTAMP=''
  COMMENT=''
  MODE="${MODE_GET}"

  while [[ "$#" -gt 0 ]]; do
    case "$1" in
      --help)
        print_help
        exit 0
        ;;
      "${ARG_CRADLE_ADMIN_TOOL_URL}")
        CRADLE_ADMIN_TOOL_URL="$2"
        shift 2
        ;;
      "${ARG_BOOK}")
        BOOK="$2"
        shift 2
        ;;
      "${ARG_START_TIMESTAMP}")
        START_TIMESTAMP="$2"
        shift 2
        ;;
      "${ARG_END_TIMESTAMP}")
        END_TIMESTAMP="$2"
        shift 2
        ;;
      "${ARG_COMMENT}")
        COMMENT="$2"
        shift 2
        ;;
      "${ARG_MODE}")
        MODE="$2"
        shift 2
        ;;
      *)
        echo " ERROR: unknown '${1}' argument"
        exit 1
    esac
  done

  export CRADLE_ADMIN_TOOL_URL="${CRADLE_ADMIN_TOOL_URL}"
  export BOOK="${BOOK}"
  export START_TIMESTAMP="${START_TIMESTAMP}"
  export END_TIMESTAMP="${END_TIMESTAMP}"
  export COMMENT="${COMMENT}"
  export MODE="${MODE}"

  echo 'Arguments:'
  echo " ${ARG_CRADLE_ADMIN_TOOL_URL} = '${CRADLE_ADMIN_TOOL_URL}'"
  echo " ${ARG_BOOK} = '${BOOK}'"
  echo " ${ARG_START_TIMESTAMP} = '${START_TIMESTAMP}'"
  echo " ${ARG_END_TIMESTAMP} = '${END_TIMESTAMP}'"
  echo " ${ARG_COMMENT} = '${COMMENT}'"
  echo " ${ARG_MODE} = '${MODE}'"

  verify_args
}

verify_utils() {
  echo "Check required utils: ${REQUIRED_UTILS[*]}"
  for util in "${REQUIRED_UTILS[@]}"; do
    if ! command -v "${util}" &> /dev/null; then
      echo " ERROR: '${util}' is not installed."
      print_help
      exit 10
    fi
  done
  echo " INFO: required utils are available"
}

verify_args() {
  echo 'Check arguments:'
  verify_mode
  verify_url
  verify_book
  verify_timestamps

  case "${MODE}" in
    "${MODE_SET}"|"${MODE_APPEND}")
      verify_not_empty_timestamps
      verify_comment
      ;;
    "${MODE_RESET}")
      verify_not_empty_timestamps
      ;;
  esac
}

verify_url() {
  local url
  url="${CRADLE_ADMIN_TOOL_URL}/${CRADLE_ADMIN_GET_ALL_BOOKS_PATH}"
  echo " INFO: GET - $url"
  if curl --head --silent "${url}" | head --lines 1 | grep --silent "200 OK"; then
    echo " INFO: '${CRADLE_ADMIN_TOOL_URL}' URL is accessible"
  else
    echo " ERROR: '${CRADLE_ADMIN_TOOL_URL}' URL is not accessible"
    print_help
    exit 2
  fi
}

verify_book() {
  if [ -z "${BOOK}" ]; then
    echo " ERROR: '${BOOK}' book can't be empty"
    exit 3
  fi

  local book_list
  url="${CRADLE_ADMIN_TOOL_URL}/${CRADLE_ADMIN_GET_ALL_BOOKS_PATH}"
  echo " INFO: GET - $url"
  book_list=$(curl --silent "${url}" | jq -r ".[].$CRADLE_ADMIN_BOOK_ID_KEY" | paste --serial --delimiters ' ')
  echo " INFO: existed books: $book_list"
  if echo "${book_list}" | grep --silent --word-regexp "${BOOK}"; then
    echo " INFO: '${BOOK}' book exists in the cradle"
  else
    echo " ERROR: '${BOOK}' book does not exist in the cradle"
    exit 3
  fi
}

verify_timestamps() {
  if [[ "${START_TIMESTAMP}" == '' || "${START_TIMESTAMP}" =~ ${TIMESTAMP_REGEX} ]]; then
    echo " INFO: '${START_TIMESTAMP}' start timestamp has correct format"
  else
    echo " ERROR: '${START_TIMESTAMP}' start timestamp has invalid format. Ensure it is in the format 'YYYY-MM-DDTHH:MM:SSZ'"
    exit 4
  fi

  if [[ "${END_TIMESTAMP}" == '' || "${END_TIMESTAMP}" =~ ${TIMESTAMP_REGEX} ]]; then
    echo " INFO: '${END_TIMESTAMP}' end timestamp has correct format"
  else
    echo " ERROR: '${END_TIMESTAMP}' end timestamp has invalid format. Ensure it is in the format 'YYYY-MM-DDTHH:MM:SSZ'"
    exit 4
  fi

  if [[ "${START_TIMESTAMP}" == '' || "${END_TIMESTAMP}" == '' ]]; then
    return
  fi

  local start_epoch
  local end_epoch
  start_epoch=$(date --date "${START_TIMESTAMP}" +%s)
  end_epoch=$(date --date "${END_TIMESTAMP}" +%s)

  if [[ ${start_epoch} -le ${end_epoch} ]]; then
    echo " INFO: '${START_TIMESTAMP}' start timestamp is less than or equal to '${END_TIMESTAMP}' end timestamp"
  else
    echo " ERROR: '${START_TIMESTAMP}' start timestamp is greater than '${END_TIMESTAMP}' end timestamp."
    exit 4
  fi
}

verify_not_empty_timestamps() {
  if [[ "${START_TIMESTAMP}" == '' || "${END_TIMESTAMP}" == '' ]]; then
    echo " ERROR: '${START_TIMESTAMP}' start timestamp and '${END_TIMESTAMP}' end timestamp mustn't be empty."
    exit 7
  fi
}

verify_comment() {
  if [ -z "${COMMENT}" ]; then
    echo " ERROR: '${COMMENT}' comment can't be empty"
    exit 5
  fi
}

verify_mode() {
  if [[ "${MODE}" != "${MODE_APPEND}" && "${MODE}" != "${MODE_SET}" && "${MODE}" != "${MODE_RESET}" && "${MODE}" != "${MODE_GET}" ]]; then
    echo " ERROR: '${MODE}' mode isn't either of values ['${MODE_APPEND}','${MODE_SET}','${MODE_RESET}','${MODE_GET}']"
    exit 6
  fi
}

escape_json() {
  local text
  local escaped_text
  text="${1}"
  escaped_text=$(echo -n "${text}" | jq -Rsa .)
  echo -n "${escaped_text}"
}

escape_http_parameter() {
  local text
  local escaped_text
  text="${1}"
  escaped_text=$(echo -n "${text}" | jq -sRr @uri)
  echo -n "${escaped_text}"
}

escape_http_parameter_test() {
  echo "Test"
  escape_http_parameter '["abc","def"]'

  exit 0
}

prepare_comment() {
  local indent
  local current_comment
  local text
  local new_comment
  local escaped_current_comment
  local escaped_comment

  current_comment="${1}"
  text="${2}"
  indent="${3}"

  escaped_comment=$(escape_json "${text}")
  echo "${indent}Append '${text}' text to '${current_comment}' current comment" >&2
  if [[ "${current_comment}" == "${CRADLE_ADMIN_DEFAULT_COMMENT}" ]]; then
    echo "${indent} INFO: '${current_comment}' current comment is default. '${CRADLE_ADMIN_DEFAULT_COMMENT}' will be removed" >&2
    new_comment="[${escaped_comment}]"
    echo "${new_comment}"
    return
  fi

  if [ -z "${current_comment}" ]; then
    echo "${indent} INFO: '${current_comment}' current comment is empty." >&2
    new_comment="[${escaped_comment}]"
    echo "${new_comment}"
    return
  fi

  if echo "${current_comment}" | jq -e 'if type == "array" then true else false end' > /dev/null 2>&1; then
    echo "${indent} INFO: '${current_comment}' is JSON string array" >&2
    if [[ "${current_comment}" == *"${escaped_comment}"* ]]; then
      echo "${indent} INFO: '${current_comment}' current comment already contains ${escaped_comment}" >&2
      new_comment=''
    else
      echo "${indent} INFO: Append '${escaped_comment}' into '${current_comment}' JSON string array" >&2
      new_comment=$(echo "${current_comment}" | jq -c ". + [${escaped_comment}]")
    fi
  else
    escaped_current_comment=$(escape_json "${current_comment}")
    if [[ "${escaped_current_comment}" == "${escaped_comment}" ]]; then
      echo "${indent} INFO: '${escaped_current_comment}' and '${escaped_comment}' are equal" >&2
      new_comment="[${escaped_current_comment}]"
    else
      echo "${indent} INFO: Wrap '${escaped_current_comment}' and '${escaped_comment}' into JSON string array" >&2
      new_comment="[${escaped_current_comment},${escaped_comment}]"
    fi
  fi

  echo "${new_comment}"
}

prepare_comment_test() {
  echo "Test"
  prepare_comment '' '"quote test"'
  echo "Test"
  prepare_comment "${CRADLE_ADMIN_DEFAULT_COMMENT}" '"quote test"'
  echo "Test"
  prepare_comment 'def' '"quote test"'
  echo "Test"
  prepare_comment '["abc"]' '"quote test"'
  echo "Test"
  prepare_comment '[1]' '"quote test"'
  echo "Test"
  prepare_comment '["\"quote test\""]' '"quote test"'
  echo "Test"
  prepare_comment '"quote comment"' '"quote test"'
  echo "Test"
  prepare_comment '"quote test"' '"quote test"'


  echo "Test"
  prepare_comment '' 'simple test'
  echo "Test"
  prepare_comment "${CRADLE_ADMIN_DEFAULT_COMMENT}" 'simple test'
  echo "Test"
  prepare_comment 'def' 'simple test'
  echo "Test"
  prepare_comment '["abc"]' 'simple test'
  echo "Test"
  prepare_comment '[1]' 'simple test'
  echo "Test"
  prepare_comment '["\"simple test\""]' 'simple test'
  echo "Test"
  prepare_comment '"quote comment"' 'simple test'
  echo "Test"
  prepare_comment 'simple test' 'simple test'

  exit 0
}

generate_jq_query() {
  echo ".[] | .[\"${CRADLE_ADMIN_PAGES_KEY}\"][] |
    select(
      (
        .[\"${CRADLE_ADMIN_ENDED_KEY}\"] == ${CRADLE_ADMIN_NULL_VALUE}
          or
        \"${START_TIMESTAMP}\" == \"\"
          or
        .[\"${CRADLE_ADMIN_ENDED_KEY}\"] > \"${START_TIMESTAMP}\"
      ) and (
        \"${END_TIMESTAMP}\" == \"\"
          or
        .[\"${CRADLE_ADMIN_STARTED_KEY}\"] <= \"${END_TIMESTAMP}\"
      )
    )"
}

update_page_comments() {
  echo "Update pages' comments:"
  local books_info_json
  local url
  local jq_query
  url="${CRADLE_ADMIN_TOOL_URL}/${CRADLE_ADMIN_GET_BOOK_INFO_PATH}?${CRADLE_ADMIN_BOOK_ID_HTTP_ARG}=${BOOK}"
  echo " INFO: GET - $url"
  books_info_json=$(curl --silent "${url}")

  jq_query="$(generate_jq_query) | \"\(.[\"${CRADLE_ADMIN_PAGE_ID_KEY}\"]) \(.[\"${CRADLE_ADMIN_COMMENT_KEY}\"])\""
  echo "${books_info_json}" | jq -r "${jq_query}" | \
  while read -r page_id comment; do
    local new_comment

    case "${MODE}" in
      "${MODE_APPEND}")
        new_comment="$(prepare_comment "${comment}" "${COMMENT}" ' ')"
        ;;
      "${MODE_SET}")
        new_comment="$(prepare_comment '' "${COMMENT}" ' ')"
        ;;
      "${MODE_RESET}")
        new_comment="${CRADLE_ADMIN_DEFAULT_COMMENT}"
        ;;
      *)
        echo " ERROR: incorrect '${MODE}' mode for update pages' comment"
        exit 20
        ;;
    esac

    if [ -z "${new_comment}" ]; then
      echo " INFO: skipped '${BOOK}.${page_id}' page comment update because '${comment}' already contains '${COMMENT}'"
    else
      local escaped_new_comment
      escaped_new_comment="$(escape_http_parameter "${new_comment}")"
      url="${CRADLE_ADMIN_TOOL_URL}/${CRADLE_ADMIN_UPDATE_PAGE_PATH}?${CRADLE_ADMIN_BOOK_ID_HTTP_ARG}=${BOOK}&${CRADLE_ADMIN_PAGE_NAME_HTTP_ARG}=${page_id}&${CRADLE_ADMIN_NEW_COMMENT_HTTP_ARG}=${escaped_new_comment}"
      echo " INFO: GET - $url"
      if curl --silent "${url}" | head --lines 1 | grep --silent "Success"; then
        echo " INFO: updated comment from '${comment}' to '${new_comment}' for '${BOOK}.${page_id}' page"
      else
        echo " ERROR: update comment for '${BOOK}.${page_id}' page failed"
      fi
    fi
  done
}

get_page_comments() {
  echo "Get pages' comments:"
  local books_info_json
  local url
  local jq_query
  url="${CRADLE_ADMIN_TOOL_URL}/${CRADLE_ADMIN_GET_BOOK_INFO_PATH}?${CRADLE_ADMIN_BOOK_ID_HTTP_ARG}=${BOOK}"
  echo " INFO: GET - $url"
  books_info_json=$(curl --silent "${url}")

  jq_query="$(generate_jq_query) | \"\(.[\"${CRADLE_ADMIN_PAGE_ID_KEY}\"]) \(.[\"${CRADLE_ADMIN_STARTED_KEY}\"]) \(.[\"${CRADLE_ADMIN_ENDED_KEY}\"]) \(.[\"${CRADLE_ADMIN_COMMENT_KEY}\"]) \""
  echo "${books_info_json}" | jq -r "${jq_query}" | \
  while read -r page_id started ended comment; do
    echo " '${BOOK}.${page_id}' [${started} - ${ended}) - '${comment}'"
  done
}

verify_utils
parse_args "$@"
case "${MODE}" in
  "${MODE_GET}")
    get_page_comments
    ;;
  *)
    update_page_comments
    ;;
esac

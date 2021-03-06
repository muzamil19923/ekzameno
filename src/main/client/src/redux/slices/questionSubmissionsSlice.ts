import { createAsyncThunk, createEntityAdapter, createSlice } from "@reduxjs/toolkit";
import { State } from "../state";
import { RootState } from "../store";
import { createExamSubmission, fetchExamSubmissions } from "./examSubmissionsSlice";

export interface QuestionSubmission {
  answer?: string,
  marks?: number,
  examSubmissionId: string,
  questionId: string,
}

export interface QuestionSubmissionState extends QuestionSubmission {
  id: string,
  meta: {
    eTag: string,
  },
}

const questionSubmissionsAdapter = createEntityAdapter<QuestionSubmissionState>();

const initialState = questionSubmissionsAdapter.getInitialState({
  status: "idle",
} as State);

export const createQuestionSubmission = createAsyncThunk(
  "questions/createSubmission",
  async ({
    questionId,
    examSubmissionId,
    answer,
    marks,
  }: {
    questionId: string,
    examSubmissionId: string,
    answer: string,
    marks?: number,
  }) => {
    const res = await fetch(`/api/questions/${questionId}/submissions/${examSubmissionId}`, {
      method: "post",
      body: JSON.stringify({ marks, answer }),
      headers: {
        "content-type": "application/json",
      },
    });

    if (!res.ok) {
      throw new Error(res.status.toString());
    }

    return res.json() as Promise<QuestionSubmissionState>;
  },
);

export const updateQuestionSubmission = createAsyncThunk(
  "questions/updateSubmission",
  async ({
    questionId,
    examSubmissionId,
    marks,
    eTag,
  }: {
    questionId: string,
    examSubmissionId: string,
    marks: number,
    eTag: string,
  }) => {
    const res = await fetch(`/api/questions/${questionId}/submissions/${examSubmissionId}`, {
      method: "put",
      body: JSON.stringify({ marks }),
      headers: {
        "content-type": "application/json",
        "if-match": eTag,
      },
    });

    if (!res.ok) {
      throw new Error(res.status.toString());
    }

    return res.json() as Promise<QuestionSubmissionState>;
  },
);

export const questionSubmissionsSlice = createSlice({
  name: "questionSubmissions",
  initialState,
  reducers: {},
  extraReducers: builder => {
    builder.addCase(fetchExamSubmissions.fulfilled, (state, action) => {
      action.payload.forEach(submission => {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        questionSubmissionsAdapter.upsertMany(state, submission.questionSubmissions);
      });
    });
    builder.addCase(createExamSubmission.fulfilled, (state, action) => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      questionSubmissionsAdapter.upsertMany(state, action.payload.questionSubmissions);
    });
    builder.addCase(createQuestionSubmission.fulfilled, (state, action) => {
      questionSubmissionsAdapter.addOne(state, action.payload);
    });
    builder.addCase(updateQuestionSubmission.fulfilled, (state, action) => {
      questionSubmissionsAdapter.upsertOne(state, action.payload);
    });
  },
});

export const {
  selectAll: selectAllQuestionSubmissions,
  selectById: selectQuestionSubmissionById,
  selectIds: selectQuestionSubmissionIds,
} = questionSubmissionsAdapter.getSelectors<RootState>(
  state => state.questionSubmissions,
);

export const selectQuestionSubmissionsForExamSubmission = (examSubmissionId?: string) => {
  return (state: RootState): QuestionSubmissionState[] => {
    return selectAllQuestionSubmissions(state)
      .filter(questionSubmission => questionSubmission.examSubmissionId === examSubmissionId);
  };
};

export const selectQuestionSubmissionsByIds = (ids: string[]) => {
  return (state: RootState): QuestionSubmissionState[] => {
    return ids
      .map(id => selectQuestionSubmissionById(state, id))
      .filter(
        questionSubmission => questionSubmission !== undefined,
      ) as QuestionSubmissionState[];
  };
};

export default questionSubmissionsSlice.reducer;
